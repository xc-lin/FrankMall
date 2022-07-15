package com.lxc.frankmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.product.entity.SkuInfoEntity;
import com.lxc.frankmall.product.vo.SkuItemVo;

import java.util.Map;

/**
 * sku信息
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 00:59:50
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    SkuItemVo item(Long skuId);
}

