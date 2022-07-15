package com.lxc.frankmall.product.vo;

import com.lxc.frankmall.product.entity.SkuImagesEntity;
import com.lxc.frankmall.product.entity.SkuInfoEntity;
import com.lxc.frankmall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author yaoxinjia
 */
@Data
public class SkuItemVo {

    //1、sku基本信息获取 pms_sku_info
    SkuInfoEntity info;
    //库存
    boolean hasStock = true;
    //2、sku图片信息    pms_sku_images
    List<SkuImagesEntity> images;
    //3、获取spu的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;
    //4、获取spu的介绍
    SpuInfoDescEntity desp;
    //5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    //6、秒杀商品的优惠信息
    private SeckillSkuVo seckillSkuVo;


}
