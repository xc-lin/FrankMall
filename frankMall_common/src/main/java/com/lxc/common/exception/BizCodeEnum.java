package com.lxc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Frank_lin
 * @date 2022/6/26
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum BizCodeEnum {

    UNKNOWN_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数校验失败"),
    SMS_CODE_EXCEPTION(10002,"短信验证码频率太高"),
    USERNAME_EXIST_EXCEPTION(15001,"用户存在"),
    PHONE_EXIST_EXCEPTION(15002,"电话已存在"),
    LOGIN_FAILED_EXCEPTION(15003,"登录失败"),
    NO_STOCK_EXCEPTION(21000,"商品库存不足");




    Integer code;
    String message;
}
