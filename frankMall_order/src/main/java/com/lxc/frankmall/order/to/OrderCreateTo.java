package com.lxc.frankmall.order.to;


import com.lxc.frankmall.order.entity.OrderEntity;
import com.lxc.frankmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yaoxinjia
 * @email 894548575@qq.com
 */
@Data
public class OrderCreateTo {

    private OrderEntity order;

    private List<OrderItemEntity> orderItems;

    /** 订单计算的应付价格 **/
    private BigDecimal payPrice;

    /** 运费 **/
    private BigDecimal fare;

}
