package com.atguigu.gmall.manage.manageservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.manage.mapper.BaseAttrInfoMapper;
import com.atguigu.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(String join) {
        return baseAttrInfoMapper.selectAttrListByValueIds(join);
    }
}
