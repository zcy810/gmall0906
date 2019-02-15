package com.atguigu.gmall.order.listener;

import com.atguigu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
@Component
public class OrderLitener {
    @Autowired
    OrderService orderService;
    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAYMENT_SUCCESS_QUEUE")
    public void consumePaymentResult(MapMessage mapMessage){
        String out_trade_no = null;
        String tracking_no = null;
        String payment_status = null;
        try {
            out_trade_no = mapMessage.getString("out_trade_no");
            tracking_no = mapMessage.getString("tracking_no");
            payment_status = mapMessage.getString("payment_status");
        } catch (JMSException e) {
            e.printStackTrace();
        }
        System.out.println(out_trade_no+"已经完成，请指示");
        orderService.updateOrder(out_trade_no,payment_status,tracking_no);
    }
}
