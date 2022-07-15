package com.lxc.frankmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lxc.frankmall.product.entity.AttrEntity;
import com.lxc.frankmall.product.service.AttrService;
import com.lxc.frankmall.product.vo.AttrGroupWithAttrsVo;
import com.lxc.frankmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.product.dao.AttrGroupDao;
import com.lxc.frankmall.product.entity.AttrGroupEntity;
import com.lxc.frankmall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrService attrService;

    @Autowired
    AttrGroupDao attrGroupDao;



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(key)) {
            wrapper.and((wrap) -> {
                wrap.eq(AttrGroupEntity::getAttrGroupId, key)
                        .or()
                        .like(AttrGroupEntity::getAttrGroupName, key);
            });

        }
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
        wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);

    }

    /**
     * 根据分类id查处所有分组以及组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new LambdaQueryWrapper<AttrGroupEntity>()
                .eq(AttrGroupEntity::getCatelogId, catelogId));
        List<AttrGroupWithAttrsVo> collect = attrGroupEntities
                .stream()
                .map(group -> {
                    AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                    BeanUtils.copyProperties(group, attrGroupWithAttrsVo);
                    List<AttrEntity> relationAttrs = attrService.getRelationAttr(attrGroupWithAttrsVo.getAttrGroupId());
                    attrGroupWithAttrsVo.setAttrs(relationAttrs);
                    return attrGroupWithAttrsVo;
                }).collect(Collectors.toList());
        // 查询所有属性

        return collect;
    }

    /**
     * 查出当前spu对应所有属性的分组信息，一级分组下所有属性的信息
     * @param spuId
     * @param catalogId
     * @return
     */
    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        List<SpuItemAttrGroupVo> vos= attrGroupDao.getAttrGroupWithAttrsBySpuId(spuId, catalogId);


        return vos;
    }

}