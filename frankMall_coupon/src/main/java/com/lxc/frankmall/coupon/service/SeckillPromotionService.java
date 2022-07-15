package com.lxc.frankmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:35:35
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

