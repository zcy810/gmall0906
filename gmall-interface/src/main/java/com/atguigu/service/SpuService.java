package com.atguigu.service;

import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface SpuService {
    List<SpuInfo> getSpuList(String catalog3Id);

    List<BaseSaleAttr> baseSaleAttrList();

    void saveSpu(SpuInfo spuInfo);

    List<SpuSaleAttr> spuSaleAttrList(String spuId);

    List<SpuImage> spuImageList(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(String spuId, String skuId);
}
