package com.atguigu.gmall.user.UserService;

import com.atguigu.gmall.user.bean.UserAdd;
import com.atguigu.gmall.user.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> getList();

    List<UserAdd> getAddById(Integer id);

    void addUser(UserInfo userInfo);

    void updateUser(Integer id, UserInfo userInfo);

    void deleteUser(Integer id);

}
