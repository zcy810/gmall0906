package com.atguigu.service;


import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> getList();

    List<UserAddress> getAddById(String id);

    void addUser(UserInfo userInfo);

    void updateUser(Integer id, UserInfo userInfo);

    void deleteUser(Integer id);

    UserInfo login(UserInfo userInfo);

    void addUserCache(UserInfo userInfofromDB);
}
