package com.lxc.frankmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.utils.PageUtils;
import com.lxc.frankmall.coupon.entity.HomeAdvEntity;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:35:35
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

