package com.atguigu.gmall.payment.serviceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.conf.ActiveMQUtil;
import com.atguigu.gmall.payment.mapper.PaymentMapper;
import com.atguigu.service.PaymentService;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentMapper paymentMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;
    @Autowired
    AlipayClient alipayClient;
    @Override
    public void save(PaymentInfo paymentInfo) {
        paymentMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("outTradeNo",paymentInfo.getOutTradeNo());
        paymentMapper.updateByExampleSelective(paymentInfo, example);
    }

    @Override
    public void sendPaymentSuccess(String outTradeNo, String paymentStatus, String trackingNo) {
        try {
            ConnectionFactory connect = activeMQUtil.getConnectionFactory();
            Connection connection = connect.createConnection();
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("out_trade_no",outTradeNo);
            activeMQMapMessage.setString("payment_status",paymentStatus);
            activeMQMapMessage.setString("tracking_no",trackingNo);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(activeMQMapMessage);
            session.commit();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean checkPaymentStatus(String out_trade_no) {
        boolean b = false;

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        PaymentInfo paymentInfo1 = paymentMapper.selectOne(paymentInfo);

        if(paymentInfo1.getPaymentStatus().equals("已支付")){
            b = true;
        }

        return b;
    }

    @Override
    public void sendDelayPaymentCheck(String outTradeNo, int i) {
        try {
            ConnectionFactory connect = activeMQUtil.getConnectionFactory();
            Connection connection = connect.createConnection();
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,1000*30);
            activeMQMapMessage.setString("out_trade_no",outTradeNo);
            activeMQMapMessage.setInt("count",i);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(activeMQMapMessage);
            session.commit();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public PaymentInfo checkPaymentResult(String out_trade_no) {
        PaymentInfo paymentInfo = new PaymentInfo();
        AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
        HashMap<String, Object> Map = new HashMap<>();
        Map.put("out_trade_no",out_trade_no);
        String s = JSON.toJSONString(Map);
        alipayTradeQueryRequest.setBizContent(s);
        AlipayTradeQueryResponse alipayTradeQueryResponse = null;
        try {
            alipayTradeQueryResponse = alipayClient.execute(alipayTradeQueryRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setOutTradeNo(out_trade_no);
        paymentInfo.setCallbackContent(alipayTradeQueryResponse.getMsg());
        if(alipayTradeQueryResponse.isSuccess()){
            System.out.println("交易已创建");
            paymentInfo.setPaymentStatus(alipayTradeQueryResponse.getTradeStatus());
            paymentInfo.setAlipayTradeNo(alipayTradeQueryResponse.getTradeNo());
        }else {
            System.out.println("交易为创建");
        }


        return paymentInfo;
    }

}
