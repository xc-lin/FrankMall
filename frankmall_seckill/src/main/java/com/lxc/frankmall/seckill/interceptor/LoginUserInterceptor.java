package com.lxc.frankmall.seckill.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxc.common.entity.MemberEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Frank_lin
 * @date 2022/7/3
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberEntity> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        if (!new AntPathMatcher().match("/kill",requestURI)) {
            return true;
        }


        HttpSession session = request.getSession();
        Object object = session.getAttribute("loginUser");
        ObjectMapper objectMapper = new ObjectMapper();
        MemberEntity memberEntity = objectMapper.convertValue(object, new TypeReference<MemberEntity>() {
        });
        if (loginUser != null) {
            loginUser.set(memberEntity);
            return true;
        } else {
            // 没登录
            session.setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
