package com.atguigu.service;

import com.atguigu.gmall.bean.OrderInfo;

public interface OrderService {
    void genTradeCode(String tradeCode, String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    void saveOrder(OrderInfo orderInfo);

     OrderInfo getOrderByOutTradeno(String outTradeNo);

    void updateOrder(String out_trade_no, String payment_status, String tracking_no);
}
