package com.lxc.frankmall.member.feign;

import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Frank_lin
 * @date 2022/6/25
 */

@FeignClient(value = "frankMall-coupon",path = "/coupon/coupon")
public interface CouponFeignService {

    @GetMapping("member/list")
    public R memberCoupons();
}
