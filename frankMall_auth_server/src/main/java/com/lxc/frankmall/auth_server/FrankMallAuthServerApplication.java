package com.lxc.frankmall.auth_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients
@SpringBootApplication
public class FrankMallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrankMallAuthServerApplication.class, args);
    }

}
