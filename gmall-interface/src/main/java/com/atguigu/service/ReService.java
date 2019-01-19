package com.atguigu.service;

import com.atguigu.gmall.bean.ReComment;

import java.util.List;

public interface ReService {
    List<ReComment> getAllList(String skuId);
}
