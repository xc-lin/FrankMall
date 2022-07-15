package com.lxc.frankmall.auth_server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.constant.AuthServerConstant;
import com.lxc.common.entity.MemberEntity;
import com.lxc.common.exception.BizCodeEnum;
import com.lxc.common.utils.Constant;
import com.lxc.common.utils.R;
import com.lxc.common.vo.UserLoginVo;
import com.lxc.common.vo.UserRegistVo;
import com.lxc.frankmall.auth_server.feignService.MemberService;
import com.lxc.frankmall.auth_server.feignService.SmsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Frank_lin
 * @date 2022/6/29
 */
@Controller
public class LoginController {


    @Autowired
    MemberService memberService;

    @Autowired
    SmsService smsService;


    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/sms/sendCode")
    @ResponseBody
    public R sendCode(@RequestParam("phone") String phone, @RequestHeader("x-forwarded-for") String addr) {
        // 接口防刷
        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone + ":" + addr;
        String s = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(s)) {
            long time = Long.parseLong(s.split("_")[1]);
            if (System.currentTimeMillis() - time < 60000L) {
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION);
            }
        }
        // 验证码的校验
        String code = UUID.randomUUID().toString().substring(0, 6) + "_" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        smsService.sendCode(phone, code.split("_")[0]);
        return R.ok();
    }

    @PostMapping("/register")
    public String register(@Validated UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes, @RequestHeader("x-forwarded-for") String addr) {
        if (result.hasErrors()) {
            Map<String, String> collect = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("error", collect);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
        // 校验验证码
        String code = vo.getCode();
        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone() + ":" + addr;
        String s = stringRedisTemplate.opsForValue().get(key);
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotEmpty(s) && s.split("_")[0].equals(code)) {
            stringRedisTemplate.delete(key);
            R regist = memberService.regist(vo);
            if (regist.getCode().equals(0)) {
                return "redirect:http://auth.gulimall.com/login.html";
            }
            map.put("code", (String) regist.get("msg"));
            redirectAttributes.addFlashAttribute("errors", map);
            return "redirect:http://auth.gulimall.com/reg.html";

        }
        map.put("code", "验证码错误");
        redirectAttributes.addFlashAttribute("error", map);

        return "redirect:http://auth.gulimall.com/reg.html";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session) {
        R login = memberService.login(vo);
        if (login.getCode().equals(0)){
            Object data = login.get("data");
            ObjectMapper objectMapper = new ObjectMapper();
            MemberEntity entity = objectMapper.convertValue(data, new TypeReference<MemberEntity>() {
            });
            session.setAttribute("loginUser",entity);
            return "redirect:http://gulimall.com";
        }else {
            Map<String , String> map = new HashMap<>();
            map.put("msg", (String) login.get("msg"));
            attributes.addFlashAttribute("errors",map);
            return "redirect:http://auth.gulimall.com/login.html";

        }


    }

}
