package com.atguigu.service;


import com.atguigu.gmall.bean.UserAdd;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> getList();

    List<UserAdd> getAddById(Integer id);

    void addUser(UserInfo userInfo);

    void updateUser(Integer id, UserInfo userInfo);

    void deleteUser(Integer id);

}
