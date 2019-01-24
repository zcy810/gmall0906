package com.atguigu.service;

import com.atguigu.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {
    void saveCart(CartInfo cartInfo);

    void flushCartCacheByUser(String userId);


    CartInfo exists(CartInfo exists);

    List<CartInfo> cartListFromCache(String userId);

    void updateCart(CartInfo info);
}
