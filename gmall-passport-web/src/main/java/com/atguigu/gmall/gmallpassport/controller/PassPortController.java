package com.atguigu.gmall.gmallpassport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.EntitySku;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.JwtUtil;
import com.atguigu.gmall.utils.MD5Utils;
import com.atguigu.service.CartService;
import com.atguigu.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {
    @Reference
    UserService userService;
    @Reference
    CartService cartService;


    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request, String requestId, String token) {
        Map userMap = JwtUtil.decode(EntitySku.KEY, token, MD5Utils.md5(requestId));
        HashMap<String, String> map = new HashMap<>();

        if (userMap != null) {//{status:success,userId:2}
            String userId = (String) userMap.get("userId");
            map.put("userId", userId);
            map.put("success", "success");
            String Entity = JSON.toJSONString(map);
            return Entity;
        } else {
            map.put("fail", "fail");
            String Entity = JSON.toJSONString(map);
            return Entity;

        }
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request, HttpServletResponse response) {
        UserInfo userInfofromDB = userService.login(userInfo);
        String token = "";
       if (userInfofromDB == null) {
             //request.setAttribute("err","账号或者密码错误");
            return "err";
        } else {

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("userId", userInfofromDB.getId());
            hashMap.put("nickName", userInfofromDB.getNickName());
            String nip = request.getHeader("request-forwared-for");
            if (StringUtils.isBlank(nip)) {
                nip = request.getRemoteAddr();// servlet中ip
                if (StringUtils.isBlank(nip)) {
                    nip = "127.0.0.1";
                }
            }
            token = JwtUtil.encode(EntitySku.KEY, hashMap, MD5Utils.md5(nip));
            userService.addUserCache(userInfofromDB);
            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            if (StringUtils.isNotBlank(listCartCookie)) {
                // cookie中有购物车数据
                cartService.mergCart(userInfofromDB.getId(), listCartCookie);
            } else {
                // cookie中没有购物车数据
                cartService.flushCartCacheByUser(userInfofromDB.getId());
            }

            // 删除cookie中的数据
            CookieUtil.deleteCookie(request, response, "listCartCookie");
        }

        return token;
    }

    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map) {

        map.put("originUrl", returnUrl);
        return "index";
    }
}
