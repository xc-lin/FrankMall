package com.lxc.frankmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.coupon.entity.SeckillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:35:35
 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

