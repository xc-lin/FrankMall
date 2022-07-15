package com.lxc.frankmall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.WebFluxCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.lxc.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Frank_lin
 * @date 2022/7/7
 */
@RestControllerAdvice
public class SeckillSentinelConfig {

    @ExceptionHandler(BlockException.class)
    public R a(){
        return R.error();
    }
}
