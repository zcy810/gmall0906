package com.atguigu.service;

import com.atguigu.gmall.bean.SpuInfo;

import java.util.List;

public interface SpuService {
    List<SpuInfo> getSpuList(String catalog3Id);
}
