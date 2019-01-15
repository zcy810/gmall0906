package com.atguigu.gmall.manage.manageservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
   private SkuInfoMapper skuInfoMapper;
    @Override
    public List<SkuInfo> selectAll() {
        return skuInfoMapper.selectAll();
    }
}
