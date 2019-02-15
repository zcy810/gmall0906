package com.atguigu.gmall.order.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.enums.PaymentWay;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.util.RedisUtil;
import com.atguigu.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Override
    public void genTradeCode(String tradeCode, String userId) {
        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:"+userId+":tradeCode",60*30,tradeCode);

        jedis.close();
    }

    @Override
    public boolean checkTradeCode(String tradeCode, String userId) {
        boolean b = false;

        Jedis jedis = redisUtil.getJedis();
        String tradeCodeFromCache = jedis.get("user:" + userId + ":tradeCode");

        if(tradeCode.equals(tradeCodeFromCache)){
            b = true;

            jedis.del("user:" + userId + ":tradeCode");
        }

        return b;
    }

    @Override
    public void saveOrder(OrderInfo orderInfo) {
        orderInfoMapper.insertSelective(orderInfo);

        String orderId = orderInfo.getId();

        // 保存订单详情
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insertSelective(orderDetail);
        }

    }

    @Override
    public OrderInfo getOrderByOutTradeno(String outTradeNo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo = orderInfoMapper.selectOne(orderInfo);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }

    @Override
    public void updateOrder(String out_trade_no, String payment_status, String tracking_no) {

        Example e = new Example(OrderInfo.class);
        e.createCriteria().andEqualTo("outTradeNo",out_trade_no);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderStatus("订单已支付");
        orderInfo.setPaymentWay(PaymentWay.ONLINE);
        orderInfo.setProcessStatus("订单已支付");
        orderInfo.setTrackingNo(tracking_no);

        orderInfoMapper.updateByExampleSelective(orderInfo,e);

    }
}
