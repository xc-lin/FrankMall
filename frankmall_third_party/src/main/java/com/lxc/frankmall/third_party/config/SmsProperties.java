package com.lxc.frankmall.third_party.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@ConfigurationProperties("sms")
@Component
@Data
public class SmsProperties {
    private String appcode;
    private String host;

}
