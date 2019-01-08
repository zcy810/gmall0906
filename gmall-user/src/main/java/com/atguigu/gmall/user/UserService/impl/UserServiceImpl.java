package com.atguigu.gmall.user.UserService.impl;

import com.atguigu.gmall.bean.UserAdd;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.service.UserService;
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

    @Override
    public void addUser(UserInfo userInfo) {
        userMapper.insert(userInfo);
    }

    @Override
    public void updateUser(Integer id, UserInfo userInfo) {
        userMapper.updateByExample(userInfo,id);
    }

    @Override
    public void deleteUser(Integer id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
