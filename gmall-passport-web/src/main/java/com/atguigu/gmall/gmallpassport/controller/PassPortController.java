package com.atguigu.gmall.gmallpassport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.EntitySku;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.util.JwtUtil;
import com.atguigu.gmall.utils.MD5Utils;
import com.atguigu.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassPortController {
    @Reference
    UserService userService;


    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request,String requestId,String token){
        Map userMap = JwtUtil.decode(EntitySku.KEY, token, MD5Utils.md5(requestId));
            HashMap<String,String> map = new HashMap<>();

        if(userMap!=null){//{status:success,userId:2}
            String userId = (String)userMap.get("userId");
            map.put("userId",userId);
            map.put("success","success");
            String Entity = JSON.toJSONString(map);
            return Entity;
        }else{
            map.put("fail","fail");
            String Entity = JSON.toJSONString(map);
            return Entity;

        }
    }


    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request){
        UserInfo userInfofromDB = userService.login(userInfo);
        if(userInfofromDB == null){
            request.setAttribute("err","账号或者密码错误");
            return "err";
        }
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("userId",userInfofromDB.getId());
        hashMap.put("nickName",userInfofromDB.getNickName());
        String nip = request.getHeader("request-forwared-for");
        if(StringUtils.isBlank(nip)){
            nip = request.getRemoteAddr();// servlet中ip
            if(StringUtils.isBlank(nip)){
                nip = "127.0.0.1";
            }
        }
        String token = JwtUtil.encode(EntitySku.KEY, hashMap, MD5Utils.md5(nip));
        userService.addUserCache(userInfofromDB);
    return token;
    }
    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map){

        map.put("originUrl",returnUrl);
        return "index";
    }
}
