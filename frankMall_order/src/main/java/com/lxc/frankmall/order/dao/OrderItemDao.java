package com.lxc.frankmall.order.dao;

import com.lxc.frankmall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:59:01
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
