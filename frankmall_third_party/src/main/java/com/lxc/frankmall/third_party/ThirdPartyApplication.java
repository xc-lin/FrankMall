package com.lxc.frankmall.third_party;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

@EnableDiscoveryClient
@SpringBootApplication
public class ThirdPartyApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ThirdPartyApplication.class, args);

    }

}
