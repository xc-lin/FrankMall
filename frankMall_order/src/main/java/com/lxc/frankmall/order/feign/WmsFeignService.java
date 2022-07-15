package com.lxc.frankmall.order.feign;

import com.lxc.common.utils.R;
import com.lxc.frankmall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@FeignClient("frankMall-ware")
public interface WmsFeignService {
    @PostMapping(value = "/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);


    @PostMapping(value = "ware/waresku/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo);
}
