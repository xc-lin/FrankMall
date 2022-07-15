package com.lxc.frankmall.coupon.dao;

import com.lxc.frankmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:35:35
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
