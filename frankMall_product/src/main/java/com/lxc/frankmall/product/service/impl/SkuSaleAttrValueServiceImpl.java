package com.lxc.frankmall.product.service.impl;

import com.lxc.frankmall.product.vo.SkuItemSaleAttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.product.dao.SkuSaleAttrValueDao;
import com.lxc.frankmall.product.entity.SkuSaleAttrValueEntity;
import com.lxc.frankmall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId) {

        List<SkuItemSaleAttrVo> skuItemSaleAttrVos =skuSaleAttrValueDao.getSaleAttrBySpuId(spuId);
        return skuItemSaleAttrVos;
    }

    @Override
    public List<String> getSkuSaleAttrValuesAsStringList(Long skuId) {
       return this.baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
    }

}