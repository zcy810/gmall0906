package com.atguigu.gmall.user.UserService.impl;

import com.atguigu.gmall.user.UserService.UserService;
import com.atguigu.gmall.user.bean.UserAdd;
import com.atguigu.gmall.user.bean.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAddMapper userAddMapper;

    @Override
    public List<UserInfo> getList() {
        return userMapper.selectAll();
    }

    @Override
    public List<UserAdd> getAddById(Integer id) {
        return userAddMapper.selectIdByAdd(id);
    }
}
