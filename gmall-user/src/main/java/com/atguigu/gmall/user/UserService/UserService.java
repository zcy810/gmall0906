package com.atguigu.gmall.user.UserService;

import com.atguigu.gmall.user.bean.UserAdd;
import com.atguigu.gmall.user.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> getList();

    List<UserAdd> getAddById(Integer id);

    public void addUser(UserInfo userInfo);

    public void updateUser(Integer id,UserInfo userInfo);

    public void deleteUser(Integer id);

}
