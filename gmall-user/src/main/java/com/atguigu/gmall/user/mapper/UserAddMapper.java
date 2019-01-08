package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.user.bean.UserAdd;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface UserAddMapper extends Mapper<UserAdd> {
    List<UserAdd> selectIdByAdd(Integer id);
}
