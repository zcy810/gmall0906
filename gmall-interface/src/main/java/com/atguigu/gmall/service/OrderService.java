package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OrderInfo;

public interface OrderService {
    void genTradeCode(String tradeCode, String userId);

    boolean checkTradeCode(String tradeCode, String userId);

    void saveOrder(OrderInfo orderInfo);

     OrderInfo getOrderByOutTradeNo(String outTradeNo);

    void updateOrder(String out_trade_no, String payment_status, String tracking_no);

    void sendOrderResult(String out_trade_no);

    void updateOrderByOrderId(String orderId, String payment_status);
}
