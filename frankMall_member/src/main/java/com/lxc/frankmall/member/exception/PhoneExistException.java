package com.lxc.frankmall.member.exception;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super("手机号已经存在");
    }
}
