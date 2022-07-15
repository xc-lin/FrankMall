package com.lxc.frankmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.frankmall.product.entity.CategoryBrandRelationEntity;
import com.lxc.frankmall.product.service.CategoryBrandRelationService;
import com.lxc.frankmall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.product.dao.CategoryDao;
import com.lxc.frankmall.product.entity.CategoryEntity;
import com.lxc.frankmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    // private Map<String, Object> cache = new HashMap<>();


    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 查处所有分类
        List<CategoryEntity> list = categoryDao.selectList(null);
        // 组装成树形分类
        //1 找到所有的一级分类

        List<CategoryEntity> level1Menu = list.stream()
                .filter((x) -> x.getParentCid() == 0)
                .map(menu -> {
                    menu.setChildren(getChildren(menu, list));
                    return menu;
                })
                .sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());
        return level1Menu;

    }

    @Override
    public void removeMenusByIds(List<Long> asList) {
        // 检查当前要删除的菜单，是否被别的地方饮用


        categoryDao.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        CategoryEntity byId = this.getById(catelogId);
        findParentId(byId.getCatId(), path);
        return path.toArray(new Long[path.size()]);
    }

    public void findParentId(Long childCatelogId, List<Long> path) {
        CategoryEntity byId = this.getById(childCatelogId);
        if (byId.getParentCid().equals(0L)) {
            path.add(childCatelogId);
            return;
        }
        findParentId(byId.getParentCid(), path);
        path.add(childCatelogId);


    }

    /**
     * 同时进行多种缓存操作
     * 指定删除某个分区下的所有数据
     * @CachePut 将最新返回的数据更新到redis中
     *  读模式：
     *      缓存穿透： 缓存空数据 cache可以解决
     *      缓存击穿：大龄兵法进来同时查询一个正好过期的数据 解决；用redisson加锁 默认没有加锁, 加上sync = true
     *      缓存雪崩：大量的key同时过期 解决：加随机事件 加上过期时间
     *  写模式： 缓存与数据库一致
     *      读写加锁
     *      引入canal，感知到mysql的更新去更新redis
     *      读多写多，直接查数据库，不要用redis
     *
     *  实时性要求高的话
     *      不要用cache
     *      读多写少，及时性，一致性要求不高的数据可以用spring-cache 只要缓存数据有过期时间就足够了
     *
     * @param category
     */
    @Caching(
            evict = {
                    @CacheEvict(value = {"category"},key = "'getLevel1Categorys'"),
                    @CacheEvict(value = {"category"},key = "'getCatalogJson'")
            }
    )
    // @CacheEvict(value = {"category"},allEntries = true)

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            RLock lock = redissonClient.getReadWriteLock("catalogJson-log").writeLock();
            lock.lock();
            try {
                // LambdaUpdateWrapper<CategoryBrandRelationEntity> wrapper = new LambdaUpdateWrapper<>();
                // wrapper.eq(CategoryBrandRelationEntity::getCatelogId,category.getCatId());
                // wrapper.set(CategoryBrandRelationEntity::getCatelogName,category.getName());
                // categoryBrandRelationService.update(wrapper);
                categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

                // redisTemplate.delete("catalogJson");
            }finally {
                lock.unlock();
            }
        }


    }

    /**
     * 需要缓存，如果缓存中，方法不用调用，如果缓存中没有，调用方法，最后将方法结果放入缓存
     * 每一个需要缓存的数据我们都来指定要放到哪个名字的缓存，（缓存的分区（按照业务类型分））
     * 默认行为：
     *      如果缓存中没有，调用方法，
     *      key: 默认自动生成：category::SimpleKey []
     *      缓存value的值：默认使用jdk序列化机制，将序列化后的数据存到redis
     *      默认ttl时间：-1
     * 自定义：
     *      指定生成的key key属性指定，接受一个spEl属性
     *      指定缓存的数据存活时间 配置文件中修改
     *      将数据保存为json数据
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> list = this.baseMapper.selectList(new LambdaQueryWrapper<CategoryEntity>()
                .eq(CategoryEntity::getParentCid, 0));
        return list;
    }

    @Cacheable(value = {"category"},key = "#root.methodName",sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() throws JsonProcessingException {

        // 将数据库的多次查询变为1次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);


        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        Map<String, List<Catelog2Vo>> catalogJson = level1Categorys.stream().collect(Collectors.toMap(k -> String.valueOf(k.getCatId()), v -> {
            // 查到这个一级分类的所有二级分类
            List<CategoryEntity> leve2List = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (leve2List != null) {
                catelog2Vos = leve2List.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName().toString());
                    List<CategoryEntity> list = getParent_cid(selectList, item.getCatId());
                    if (list != null) {
                        List<Catelog2Vo.Catelog3Vo> collect1 = list.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect1);
                    }


                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return catalogJson;
    }


    // 查出所有分类

    public Map<String, List<Catelog2Vo>> getCatalogJson2() throws JsonProcessingException {
        // 给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型
        // 缓存中存的数据都是json
        // json都能兼容

        ValueOperations<String, String> cache = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        String catalogJson = cache.get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            // 查询数据库
            Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedissonLock();

            return catalogJsonFromDB;
        }
        Map<String, List<Catelog2Vo>> stringListMap = objectMapper.readValue(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return stringListMap;
    }

    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Catelog2Vo>> catalogJson;
        // 占分布式锁
        RLock lock = redissonClient.getReadWriteLock("catalogJson-log").readLock();
        lock.lock();
        try {

            String catalogJsonString = redisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJsonString)) {
                // 如果缓存不为空，直接返回

                Map<String, List<Catelog2Vo>> stringListMap = objectMapper.readValue(catalogJsonString, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return stringListMap;
            }

            // 将数据库的多次查询变为1次
            List<CategoryEntity> selectList = this.baseMapper.selectList(null);


            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            catalogJson = level1Categorys.stream().collect(Collectors.toMap(k -> String.valueOf(k.getCatId()), v -> {
                // 查到这个一级分类的所有二级分类
                List<CategoryEntity> leve2List = getParent_cid(selectList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (leve2List != null) {
                    catelog2Vos = leve2List.stream().map(item -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName().toString());
                        List<CategoryEntity> list = getParent_cid(selectList, item.getCatId());
                        if (list != null) {
                            List<Catelog2Vo.Catelog3Vo> collect1 = list.stream().map(l3 -> {
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect1);
                        }


                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            String s = objectMapper.writeValueAsString(catalogJson);
            // 将对象转为json
            redisTemplate.opsForValue().set("catalogJson", s);

        } finally {
            lock.unlock();
        }
        return catalogJson;
    }



    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedisLock() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<Catelog2Vo>> catalogJson;
        // 占分布式锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid);
        while (!lock) {
            try {
                TimeUnit.MILLISECONDS.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30L, TimeUnit.SECONDS);
        }
        try {

            String catalogJsonString = redisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJsonString)) {
                // 如果缓存不为空，直接返回

                Map<String, List<Catelog2Vo>> stringListMap = objectMapper.readValue(catalogJsonString, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return stringListMap;
            }

            // 将数据库的多次查询变为1次
            List<CategoryEntity> selectList = this.baseMapper.selectList(null);


            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            catalogJson = level1Categorys.stream().collect(Collectors.toMap(k -> String.valueOf(k.getCatId()), v -> {
                // 查到这个一级分类的所有二级分类
                List<CategoryEntity> leve2List = getParent_cid(selectList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (leve2List != null) {
                    catelog2Vos = leve2List.stream().map(item -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName().toString());
                        List<CategoryEntity> list = getParent_cid(selectList, item.getCatId());
                        if (list != null) {
                            List<Catelog2Vo.Catelog3Vo> collect1 = list.stream().map(l3 -> {
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect1);
                        }


                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            String s = objectMapper.writeValueAsString(catalogJson);
            // 将对象转为json
            redisTemplate.opsForValue().set("catalogJson", s);

        } finally {

            //需要lua脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            // 原子删除
            redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
        }

        // String lock1 = redisTemplate.opsForValue().get("lock");
        // if (uuid.equals(lock1)){
        //     redisTemplate.delete("lock");
        // }


        return catalogJson;
    }


    private Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithLocalLock() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        synchronized (this) {
            // 得到锁之后，需要再去缓存中确定一次，如果没有才需要继续查询
            // TODO 本地锁
            String catalogJsonString = redisTemplate.opsForValue().get("catalogJson");
            if (!StringUtils.isEmpty(catalogJsonString)) {
                // 如果缓存不为空，直接返回

                Map<String, List<Catelog2Vo>> stringListMap = objectMapper.readValue(catalogJsonString, new TypeReference<Map<String, List<Catelog2Vo>>>() {
                });
                return stringListMap;
            }

            Map<String, List<Catelog2Vo>> catalogJson;
            // 将数据库的多次查询变为1次
            List<CategoryEntity> selectList = this.baseMapper.selectList(null);


            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
            catalogJson = level1Categorys.stream().collect(Collectors.toMap(k -> String.valueOf(k.getCatId()), v -> {
                // 查到这个一级分类的所有二级分类
                List<CategoryEntity> leve2List = getParent_cid(selectList, v.getCatId());
                List<Catelog2Vo> catelog2Vos = null;
                if (leve2List != null) {
                    catelog2Vos = leve2List.stream().map(item -> {
                        Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName().toString());
                        List<CategoryEntity> list = getParent_cid(selectList, item.getCatId());
                        if (list != null) {
                            List<Catelog2Vo.Catelog3Vo> collect1 = list.stream().map(l3 -> {
                                Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(item.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                return catelog3Vo;
                            }).collect(Collectors.toList());
                            catelog2Vo.setCatalog3List(collect1);
                        }


                        return catelog2Vo;
                    }).collect(Collectors.toList());
                }
                return catelog2Vos;
            }));
            String s = objectMapper.writeValueAsString(catalogJson);
            // 将对象转为json
            redisTemplate.opsForValue().set("catalogJson", s);

            return catalogJson;
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return collect;
    }


    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        if (root == null) {
            return null;
        }
        List<CategoryEntity> children = all.stream()
                .filter(x -> x.getParentCid().equals(root.getCatId()))
                .map(menu -> {
                    menu.setChildren(getChildren(menu, all));
                    return menu;
                })
                .sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());


        return children;
    }

}
