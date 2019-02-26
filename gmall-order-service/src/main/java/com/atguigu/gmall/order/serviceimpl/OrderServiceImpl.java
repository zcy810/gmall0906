package com.atguigu.gmall.order.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OrderDetail;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.enums.PaymentWay;
import com.atguigu.gmall.conf.ActiveMQUtil;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.util.RedisUtil;
import com.atguigu.gmall.service.OrderService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;
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
    public OrderInfo getOrderByOutTradeNo(String outTradeNo) {
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

    @Override
    public void sendOrderResult(String out_trade_no) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOutTradeNo(out_trade_no);
        OrderInfo order = orderInfoMapper.selectOne(orderInfo);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(order.getId());
        List<OrderDetail> orderDetailList = orderDetailMapper.select(orderDetail);
        order.setOrderDetailList(orderDetailList);
        try {
            ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue testqueue = session.createQueue("ORDER_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(testqueue);
            TextMessage textMessage=new ActiveMQTextMessage();

            textMessage.setText(JSON.toJSONString(order));

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit();
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void updateOrderByOrderId(String orderId, String payment_status) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);
        orderInfo.setOrderStatus(payment_status);
        Example example = new Example(OrderInfo.class);
        example.createCriteria().andEqualTo("order_status","已出库");
        orderInfoMapper.updateByExample(orderInfo,example);
    }
}
