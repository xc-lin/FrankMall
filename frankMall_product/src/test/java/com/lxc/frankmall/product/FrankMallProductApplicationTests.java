package com.lxc.frankmall.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.frankmall.product.dao.AttrGroupDao;
import com.lxc.frankmall.product.dao.SkuSaleAttrValueDao;
import com.lxc.frankmall.product.entity.BrandEntity;
import com.lxc.frankmall.product.service.BrandService;
import com.lxc.frankmall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.sql.Wrapper;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class FrankMallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    CategoryService categoryService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    public void testStringRedisTemplate() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        stringStringValueOperations.set("hello", "world" + UUID.randomUUID().toString());

        String hello = stringStringValueOperations.get("hello");
        System.out.println("查询：" + hello);
    }

    @Test
    public void testRedissonClient() {
        RLock linxc = redissonClient.getLock("linxc");
        linxc.lock(10,TimeUnit.SECONDS);
        try {
            System.out.println("111111");

            try {
                TimeUnit.MILLISECONDS.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } finally {


            linxc.unlock();
        }


    }

    @Test
    public void testRedissonClient2() {
        RLock linxc = redissonClient.getLock("linxc");
        linxc.lock();
        try {
            System.out.println("2222222");
        } finally {
            linxc.unlock();
        }
    }


    @Test
    public void testRSemaphoreAcquire() throws InterruptedException {

        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        semaphore.acquire();
    }

    @Test
    public void testRSemaphoreRelease() throws InterruptedException {

        RSemaphore semaphore = redissonClient.getSemaphore("semaphore");
        semaphore.release();
    }

    @Test
    void contextLoads() {


    }

    @Test
    public void testFindCatelogPath() {
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info(Arrays.toString(catelogPath));
    }


    @Test
    public void testBrandService() {
        // BrandEntity brandEntity = new BrandEntity();
        // brandEntity.setDescript("car,.,,");
        // brandEntity.setName("benz");
        // brandService.save(brandEntity);


        // UpdateWrapper<BrandEntity> brandEntityUpdateWrapper = new UpdateWrapper<>();
        //
        // brandEntityUpdateWrapper.eq("name","benz").set("name","porsche");
        // brandService.update(brandEntityUpdateWrapper);


        LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(BrandEntity::getName, "or");
        List<BrandEntity> list = brandService.list(queryWrapper);
        System.out.println(list);
    }

    @Test
    public void testkk(){
        System.out.println(attrGroupDao.getAttrGroupWithAttrsBySpuId(4L, 225L));
    }

    @Test
    public void testuu(){
        System.out.println(skuSaleAttrValueDao.getSaleAttrBySpuId(4L));
    }



}
