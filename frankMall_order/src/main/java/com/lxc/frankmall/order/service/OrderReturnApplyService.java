package com.lxc.frankmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.order.entity.OrderReturnApplyEntity;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:59:01
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

