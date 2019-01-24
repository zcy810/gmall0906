package com.atguigu.gmall.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.service.UserService;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Reference
    private UserService userService;

    @RequestMapping("get/list")
    @ResponseBody
    public List<UserInfo> getList(){

        return userService.getList();
    }
    @RequestMapping("get/Add/by/id/{id}")
    @ResponseBody
    public List<UserAddress> getAddById(@PathVariable("id") String id){
        return userService.getAddById(id);
    }
    @RequestMapping("add/user")
    public String AddUser(UserInfo userInfo){
        userService.addUser(userInfo);
        return "";
    }
    @RequestMapping("delete/user")
    public String deleteUser(@RequestParam("id") Integer id){
        userService.deleteUser(id);
        return "";
    }
    @RequestMapping("update/user")
    public String updateUser(@RequestParam("id") Integer id,UserInfo userInfo){
        userService.updateUser(id, userInfo);
        return "";
    }

}
