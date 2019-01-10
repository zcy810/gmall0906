package com.atguigu.service;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseCatalog1;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;

import java.util.List;

public interface ManageService {

    List<BaseCatalog1> getAll();

    List<BaseCatalog2> getLog1(String catalog1Id);

    List<BaseCatalog3> getLog3(String catalog2Id);

    List<BaseAttrInfo> getAttrList(String catalog3Id);

    BaseAttrInfo getAttrInfo(String id);
}
