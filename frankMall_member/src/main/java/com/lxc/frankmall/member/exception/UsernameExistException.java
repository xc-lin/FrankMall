package com.lxc.frankmall.member.exception;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
public class UsernameExistException extends RuntimeException {
    public UsernameExistException() {
        super("用户名已经存在");
    }
}
