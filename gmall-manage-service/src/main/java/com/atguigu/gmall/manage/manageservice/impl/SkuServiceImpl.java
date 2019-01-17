package com.atguigu.gmall.manage.manageservice.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SkuImageMapper;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.util.RedisUtil;
import com.atguigu.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

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
    @Autowired
    private RedisUtil redisUtil;

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
    public SkuInfo getSkuInfo(String skuId, String ip) {
        SkuInfo skuInfo = null;
        Jedis jedis = redisUtil.getJedis();
        String str = jedis.get("sku:" + skuId + ":info");
        skuInfo = JSON.parseObject(str, SkuInfo.class);

        if(skuInfo == null){
            System.out.println("我这是从数据库中查的哦!!!");
            skuInfo =getSkuInfoFromDb(skuId);
            jedis.set("sku:" + skuId + ":info",JSON.toJSONString(skuInfo));
        }
        jedis.close();

        return skuInfo;
    }

    public SkuInfo getSkuInfoFromDb(String skuId) {
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

    @Override
    public List<SkuInfo> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }
}
