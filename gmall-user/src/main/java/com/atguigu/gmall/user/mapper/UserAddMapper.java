package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.user.bean.UserAdd;

import java.util.List;


public interface UserAddMapper {
    List<UserAdd> selectIdByAdd(Integer id);
}
