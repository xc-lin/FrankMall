package com.lxc.cart.vo;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;

/**
 * @author Frank_lin
 * @date 2022/7/2
 */
@JsonIgnoreProperties("username")
public class Test {
    private String username;
    @JsonIgnore
    private String password;

    public String getUsername() {
        return "温州";
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return "你好";
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
