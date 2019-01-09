package com.atguigu.service;

import com.atguigu.gmall.bean.BaseCatalog1;
import com.atguigu.gmall.bean.BaseCatalog2;

import java.util.List;

public interface ManageService {

    List<BaseCatalog1> getAll();

    List<BaseCatalog2> getLog1(String catalog1Id);
}
