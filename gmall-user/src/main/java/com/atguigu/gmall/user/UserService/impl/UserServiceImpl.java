package com.atguigu.gmall.user.UserService.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.EntitySku;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.util.RedisUtil;
import com.atguigu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserAddMapper userAddMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public List<UserInfo> getList() {

        return userMapper.selectAll();
    }

    @Override
    public List<UserAddress> getAddById(String id) {
        UserAddress userAdd = new UserAddress();
        userAdd.setUserId(id);
      return   userAddMapper.select(userAdd);
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

    @Override
    public UserInfo login(UserInfo userInfo) {
        Jedis jedis = redisUtil.getJedis();
        String s = jedis.get(EntitySku.USER_PREFIX + userInfo.getId() + EntitySku.SKU_SUFFIX);
        UserInfo userLogin = JSON.parseObject(s, UserInfo.class);
        if(userLogin == null) {
            UserInfo info = new UserInfo();
            info.setLoginName(userInfo.getLoginName());
            info.setPasswd(userInfo.getPasswd());
            userLogin = userMapper.selectOne(info);
        }
        String id = userLogin.getId();

        UserAddress userAdd = new UserAddress();
        userAdd.setUserId(id);
        List<UserAddress> select = userAddMapper.select(userAdd);
        userLogin.setUserAddressList(select);
        return userLogin;
    }

    @Override
    public void addUserCache(UserInfo userInfofromDB) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex(EntitySku.USER_PREFIX + userInfofromDB.getId() + EntitySku.SKU_SUFFIX,60*60*24, JSON.toJSONString(userInfofromDB));
        jedis.close();
    }
}
