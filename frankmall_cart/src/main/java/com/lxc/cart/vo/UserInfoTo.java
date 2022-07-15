package com.lxc.cart.vo;

import lombok.Data;

/**
 * @author Frank_lin
 * @date 2022/7/1
 */
@Data
public class UserInfoTo {

    private Long userId;
    private String userKey;

    private Boolean tempUser = false;
}
