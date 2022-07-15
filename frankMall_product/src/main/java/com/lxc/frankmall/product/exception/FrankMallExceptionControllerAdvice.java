package com.lxc.frankmall.product.exception;

import com.lxc.common.exception.BizCodeEnum;
import com.lxc.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Frank_lin
 * @date 2022/6/26
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.lxc.frankmall.product.controller")
public class FrankMallExceptionControllerAdvice {
    //集中处理所有异常
    // @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e) {
        log.error(e.toString());
        return R.error(BizCodeEnum.UNKNOWN_EXCEPTION).put("data",e);
    }


    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handleValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String,String> map= new HashMap<>();
        result.getFieldErrors().forEach((x)->{
            map.put(x.getField(),x.getDefaultMessage());
        });
        log.error("数据校验出现问题:{},异常类型{}", e.getMessage(), e.getClass());
        return R.error(BizCodeEnum.VALID_EXCEPTION).put("data",map);
    }



}
