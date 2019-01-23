package com.atguigu.gmall.cart.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.EntitySku;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.util.RedisUtil;
import com.atguigu.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartInfoMapper cartInfoMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public void saveCart(CartInfo cartInfo) {
        cartInfoMapper.insertSelective(cartInfo);
    }

    @Override
    public void flushCartCacheByUser(String userId) {
       List<CartInfo> cartInfos = getCartInfoByUserId(userId);
        Jedis jedis = redisUtil.getJedis();
        if(cartInfos != null){
            HashMap<String,String> hashMap = new HashMap<>();
            for (CartInfo cartInfo : cartInfos) {
                hashMap.put(cartInfo.getId(),JSON.toJSONString(cartInfo));

            }
            jedis.hmset(EntitySku.CART_SKU_PREFIX + userId + EntitySku.SKU_SUFFIX,hashMap);
        }
        jedis.close();

    }

    private List<CartInfo> getCartInfoByUserId(String userId) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        return  cartInfoMapper.select(cartInfo);
    }

    @Override
    public CartInfo exists(CartInfo exists) {
        return cartInfoMapper.selectOne(exists);
    }

    @Override
    public List<CartInfo> cartListFromCache(String userId) {

        List<CartInfo> cartInfos = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> hvals = jedis.hvals(EntitySku.CART_SKU_PREFIX + userId + EntitySku.SKU_SUFFIX);
        for (String hval : hvals) {
            CartInfo cartInfo = JSON.parseObject(hval, CartInfo.class);
            cartInfos.add(cartInfo);
        }
        return cartInfos;
    }

    @Override
    public void updateCart(CartInfo info) {
        Example example = new Example(CartInfo.class);
        example.createCriteria().andEqualTo("skuId",info.getSkuId()).andEqualTo("userId",info.getUserId());
        cartInfoMapper.updateByExampleSelective(info,example);
    }


}
