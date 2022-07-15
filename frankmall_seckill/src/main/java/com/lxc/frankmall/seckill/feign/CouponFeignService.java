package com.lxc.frankmall.seckill.feign;

import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */
@FeignClient("frankMall-coupon")
public interface CouponFeignService {
    @GetMapping("/coupon/seckillsession/latest3DaySession")
    R getLatest3DaySession();
}
