package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.user.UserService.UserService;
import com.atguigu.gmall.user.bean.UserAdd;
import com.atguigu.gmall.user.bean.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("get/list")
    @ResponseBody
    public List<UserInfo> getList(){

        return userService.getList();
    }
    @RequestMapping("get/Add/by/id/{id}")
    @ResponseBody
    public List<UserAdd> getAddById(@PathVariable("id") Integer id){
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
