package com.lxc.frankmall.product.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.utils.R;
import com.lxc.frankmall.product.entity.SkuImagesEntity;
import com.lxc.frankmall.product.entity.SpuInfoDescEntity;
import com.lxc.frankmall.product.feign.SeckillFeignService;
import com.lxc.frankmall.product.service.*;
import com.lxc.frankmall.product.vo.SeckillSkuVo;
import com.lxc.frankmall.product.vo.SkuItemSaleAttrVo;
import com.lxc.frankmall.product.vo.SkuItemVo;
import com.lxc.frankmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.utils.Query;

import com.lxc.frankmall.product.dao.SkuInfoDao;
import com.lxc.frankmall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;


/**
 * @author lxc
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    SkuImagesService imagesService;

    @Autowired
    SeckillFeignService seckillFeignService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.eq("sku_id", key)
                        .or()
                        .like("sku_name", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            wrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && new BigDecimal(max).compareTo(BigDecimal.ZERO) == 1) {
            wrapper.le("price", max);
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        // sku基本信息获取 sku_info
        CompletableFuture<SkuInfoEntity> info = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity info2 = getById(skuId);
            skuItemVo.setInfo(info2);
            return info2;
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrFuture = info.thenAcceptAsync((result) -> {
            // 获取spu的销售属性组合
            List<SkuItemSaleAttrVo> skuItemSaleAttrVos = skuSaleAttrValueService.getSaleAttrBySpuId(result.getSpuId());
            skuItemVo.setSaleAttr(skuItemSaleAttrVos);
        }, threadPoolExecutor);
        CompletableFuture<Void> DescFuture = info.thenAcceptAsync((result) -> {
            // 获取spu的介绍
            SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(result.getSpuId());
            skuItemVo.setDesp(spuInfoDesc);
        }, threadPoolExecutor);

        CompletableFuture<Void> attrGroupFuture = info.thenAcceptAsync((result) -> {
            // 获取spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(result.getSpuId(), result.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, threadPoolExecutor);
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            // sku图片信息  sku_images
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, threadPoolExecutor);

        CompletableFuture<Void> getSkuSeckillInfoFuture = CompletableFuture.runAsync(() -> {
            // 查询当前sku是否参与秒杀
            R r = seckillFeignService.getSkuSeckillInfo(skuId);
            if (r.getCode() == 0) {
                Object data = r.get("data");
                SeckillSkuVo seckillSkuVo = new ObjectMapper().convertValue(data, SeckillSkuVo.class);
                skuItemVo.setSeckillSkuVo(seckillSkuVo);
            }
        }, threadPoolExecutor);


        // 等待所有任务完成
        CompletableFuture.allOf(saleAttrFuture, DescFuture, attrGroupFuture, imageFuture, getSkuSeckillInfoFuture).join();


        return skuItemVo;
    }


}