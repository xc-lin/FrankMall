package com.lxc.frankmall.third_party.contrller;

import com.lxc.common.utils.R;
import com.lxc.frankmall.third_party.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;


    /**
     * 提供服务调用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code) {
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}
