package com.lxc.frankmall.auth_server.feignService;

import com.lxc.common.utils.R;
import com.lxc.common.vo.UserLoginVo;
import com.lxc.common.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@FeignClient("frankMall-member")
public interface MemberService {


    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);
}
