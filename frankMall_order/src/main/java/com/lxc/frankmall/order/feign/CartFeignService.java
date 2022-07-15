package com.lxc.frankmall.order.feign;

import com.lxc.frankmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@FeignClient("frankMall-cart")
public interface CartFeignService {

    @GetMapping("/currentUserCartItems")
    public List<OrderItemVo> getCurrentUserCartItems();
}
