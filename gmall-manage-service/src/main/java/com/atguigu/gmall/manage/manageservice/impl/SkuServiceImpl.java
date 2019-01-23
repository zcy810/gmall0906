package com.atguigu.gmall.manage.manageservice.impl;


import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
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
        //ip是用来看使用者ip的,暂时没有必要
        SkuInfo skuInfo = null;
        Jedis jedis = redisUtil.getJedis();
        String str = jedis.get(EntitySku.SKU_PREFIX + skuId + EntitySku.SKU_SUFFIX);
        skuInfo = JSON.parseObject(str, SkuInfo.class);

        if(skuInfo == null){

            String Flag = jedis.set(EntitySku.SKU_PREFIX + skuId + EntitySku.SKU_SUFFIX_LOCK, "2", "nx", "px", 5000);

            if(StringUtils.isBlank(Flag)){
                System.out.println("没拿到锁,3s后开始自旋");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return getSkuInfo(skuId,ip);
            }else {
                System.out.println("我这是从数据库中查的哦!!!");
                skuInfo =getSkuInfoFromDb(skuId);
            }
            //拿到数据为了体验自旋,让查到的数据睡一会
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jedis.del(EntitySku.SKU_PREFIX + skuId + EntitySku.SKU_SUFFIX_LOCK);
            jedis.set(EntitySku.SKU_PREFIX + skuId + EntitySku.SKU_SUFFIX,JSON.toJSONString(skuInfo));
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

    @Override
    public List<SkuInfo> getSkuListByCatalog3Id(String s) {

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(s);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : skuInfos) {
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(info.getId());
            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);
            info.setSkuAttrValueList(skuAttrValues);
        }
        return skuInfos;
    }

            @Override
            public SkuInfo getSkuBySkuId(String skuId) {


                return   skuInfoMapper.selectByPrimaryKey(skuId);
            }



}
