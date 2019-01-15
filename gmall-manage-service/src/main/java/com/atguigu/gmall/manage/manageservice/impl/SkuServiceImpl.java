package com.atguigu.gmall.manage.manageservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SkuImageMapper;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.atguigu.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
   private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Override
    public List<SkuInfo> selectAll() {
        return skuInfoMapper.selectAll();
    }

    @Override
    public void setSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);
        String skuId = skuInfo.getId();

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insertSelective(skuImage);
        }
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insertSelective(skuAttrValue);
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {

            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
        }
    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> list = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(list);

        SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
        skuSaleAttrValue.setSkuId(skuId);
        List<SkuSaleAttrValue> select = skuSaleAttrValueMapper.select(skuSaleAttrValue);
        skuInfo.setSkuSaleAttrValueList(select);

        return skuInfo;
    }
}
