package com.lxc.frankmall.order.dao;

import com.lxc.frankmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:59:00
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
