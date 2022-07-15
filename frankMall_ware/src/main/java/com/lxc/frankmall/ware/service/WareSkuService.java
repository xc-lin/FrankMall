package com.lxc.frankmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.to.OrderTo;
import com.lxc.common.to.mq.StockLockedTo;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.ware.entity.WareSkuEntity;
import com.lxc.frankmall.ware.vo.SkuHasStockVo;
import com.lxc.frankmall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 11:15:07
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    boolean orderLockStock(WareSkuLockVo vo);


    void unLockStock(StockLockedTo to);

    void unLock(OrderTo to);
}

