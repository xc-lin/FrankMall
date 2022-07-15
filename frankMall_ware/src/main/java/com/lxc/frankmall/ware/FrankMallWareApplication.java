package com.lxc.frankmall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRabbit
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.lxc.frankmall.ware.feign")
@SpringBootApplication
public class FrankMallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrankMallWareApplication.class, args);
    }

}
