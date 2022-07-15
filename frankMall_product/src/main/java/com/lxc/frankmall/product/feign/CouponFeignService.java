package com.lxc.frankmall.product.feign;

import com.lxc.common.to.SkuReductionTo;
import com.lxc.common.to.SpuBoundTo;
import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Frank_lin
 * @date 2022/6/27
 */
@FeignClient(value = "frankMall-coupon")
public interface CouponFeignService {


    /**
     * CouponFeignService.saveSpuBounds(spuBoundTo)
     * @RequestBody 将这个对象转为json
     * 找到frankMall-coupon这个服务，给coupon/spubounds/save发送请求，将上一步转的json放在请求体位置，发送请求
     * 对方服务收到请求，请求体里有json数据，@RequestBody 将请求体的json转成自己的对象
     * 只要json数据类型是兼容的，双方服务无需使用同一个to
     * @param spuBoundTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
