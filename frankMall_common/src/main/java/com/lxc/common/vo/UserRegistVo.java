package com.lxc.common.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@Data
public class UserRegistVo {

    @NotEmpty(message = "username必须提交")
    @Length(min = 1,max = 18,message = "用户名必须为6-18位字符")
    private String userName;
    @NotEmpty(message = "密码必须提交")
    @Length(min = 1,max = 18,message = "密码必须为6-18位字符")
    private String passWord;
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    @NotEmpty(message ="手机号必须提交" )
    private String phone;
    @NotEmpty(message ="验证码必须提交" )
    private String code;


}
