package com.atguigu.service;

import com.atguigu.gmall.bean.SkuInfo;

import java.util.List;

public interface SkuService {
    List<SkuInfo> selectAll();

    void setSkuInfo(SkuInfo skuInfo);

    SkuInfo getSkuInfo(String skuId,String ip);

    List<SkuInfo> getSkuSaleAttrValueListBySpu(String spuId);

    List<SkuInfo> getSkuListByCatalog3Id(String s);
}
