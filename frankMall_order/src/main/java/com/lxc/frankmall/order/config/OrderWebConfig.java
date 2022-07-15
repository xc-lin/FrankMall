package com.lxc.frankmall.order.config;

import com.lxc.frankmall.order.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@Configuration
public class OrderWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new LoginUserInterceptor());
    }
}
