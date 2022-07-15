package com.lxc.frankmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxc.common.entity.MemberEntity;
import com.lxc.common.utils.PageUtils;
import com.lxc.common.vo.UserLoginVo;
import com.lxc.common.vo.UserRegistVo;
import com.lxc.frankmall.member.exception.PhoneExistException;
import com.lxc.frankmall.member.exception.UsernameExistException;

import java.util.Map;

/**
 * 会员
 *
 * @author Franklin
 * @email xianchaolin@126.com
 * @date 2022-06-25 10:46:09
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(UserRegistVo vo);


    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernaemUnique(String username) throws UsernameExistException;

    MemberEntity login(UserLoginVo vo);
}

