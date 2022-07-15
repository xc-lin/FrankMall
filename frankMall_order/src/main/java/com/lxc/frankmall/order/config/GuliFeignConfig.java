package com.lxc.frankmall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@Configuration
public class GuliFeignConfig {

    @Bean
    RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){

            @Override
            public void apply(RequestTemplate requestTemplate) {
                System.out.println("feign 远程之前先进行RequestInterceptor.apply ");
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();
                // 同步请求头信息
                String cookie = request.getHeader("Cookie");
                // 给新请求同步了老请求的cookie
                requestTemplate.header("Cookie",cookie);
            }
        };
    }
}
