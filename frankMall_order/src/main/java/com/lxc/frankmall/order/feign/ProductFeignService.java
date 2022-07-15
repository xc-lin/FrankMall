package com.lxc.frankmall.order.feign;

import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Frank_lin
 * @date 2022/7/4
 */
@FeignClient("frankMall-product")
public interface ProductFeignService {

    @GetMapping("/product/spuinfo/{skuId}/up")
    R getSpuInfoBySkuId(@PathVariable("skuId") Long skuId);
}
