package com.lxc.frankmall.ware.feign;

import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@FeignClient("frankMall-member")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    public R addrInfo(@PathVariable("id") Long id);
}
