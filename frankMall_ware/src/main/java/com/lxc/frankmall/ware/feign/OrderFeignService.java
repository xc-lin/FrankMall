package com.lxc.frankmall.ware.feign;

import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Frank_lin
 * @date 2022/7/6
 */
@FeignClient("frankMall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
