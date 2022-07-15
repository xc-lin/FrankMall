package com.lxc.frankmall.auth_server.feignService;

import com.lxc.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@FeignClient("frankmall-third-party")
public interface SmsService {

    @GetMapping("/sms/sendCode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
