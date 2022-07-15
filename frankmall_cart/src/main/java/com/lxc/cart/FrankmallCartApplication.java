package com.lxc.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRedisHttpSession
@EnableFeignClients
@EnableTransactionManagement
@EnableDiscoveryClient
@SpringBootApplication
public class FrankmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrankmallCartApplication.class, args);
    }

}
