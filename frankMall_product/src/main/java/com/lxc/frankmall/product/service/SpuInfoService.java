package com.lxc.frankmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.product.entity.SpuInfoDescEntity;
import com.lxc.frankmall.product.entity.SpuInfoEntity;
import com.lxc.frankmall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 00:59:50
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

