package com.lxc.frankmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.to.mq.SeckillOrderTo;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.order.entity.OrderEntity;
import com.lxc.frankmall.order.vo.OrderConfirmVo;
import com.lxc.frankmall.order.vo.OrderSubmitVo;
import com.lxc.frankmall.order.vo.SubmitOrderResponseVo;

import java.util.Map;

/**
 * 订单
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:59:00
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity entity);

    void createSeckillOrder(SeckillOrderTo orderTo);
}

