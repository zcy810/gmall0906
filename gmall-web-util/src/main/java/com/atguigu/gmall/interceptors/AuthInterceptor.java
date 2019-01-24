package com.atguigu.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotation.LoginRequired;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.utils.HttpClientUtil;
import com.atguigu.gmall.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        String token ="";
        String newToken = request.getParameter("newToken");
        String oldToken = CookieUtil.getCookieValue(request,"oldToken",true);
        if(StringUtils.isNotBlank(oldToken)){
            token = oldToken;
        }
        if(StringUtils.isNotBlank(newToken)){
            token = newToken;
        }
        if(methodAnnotation != null){
            boolean loginCheck = false;
            if(StringUtils.isNotBlank(token)){
                String nip = request.getHeader("request-forwared-for");// nginx中的
                if(StringUtils.isBlank(nip)){
                    nip = request.getRemoteAddr();// servlet中ip
                    if(StringUtils.isBlank(nip)){
                        nip = "127.0.0.1";
                    }
                }
                String Entity = HttpClientUtil.doGet("http://passport.gmall.com:8089/verify?token="+token+"&requestId="+nip);
                HashMap hashMap = JSON.parseObject(Entity, HashMap.class);
                String success = (String) hashMap.get("success");
                if(success != null){
                    loginCheck = true;
                    CookieUtil.setCookie(request,response,"oldToken",token,60*24*24, true);
                    String userId = (String) hashMap.get("userId");
                    request.setAttribute("userId",userId);
                }
            }
            if (loginCheck == false && methodAnnotation.isNeedLogin() == true) {
                response.sendRedirect("http://passport.gmall.com:8089/index?returnUrl=" + request.getRequestURL());
                return false;
            }
        }

        return true;
    }
}
