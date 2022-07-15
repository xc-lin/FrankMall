package com.lxc.frankmall.third_party;

import com.lxc.frankmall.third_party.component.SmsComponent;
import org.apache.http.HttpResponse;
import com.lxc.frankmall.third_party.util.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@SpringBootTest
public class Sms {


    @Autowired
    SmsComponent smsComponent;
    @Test
    public void sendSms(){
        smsComponent.sendSmsCode("15868892557","6666");

    }
}
