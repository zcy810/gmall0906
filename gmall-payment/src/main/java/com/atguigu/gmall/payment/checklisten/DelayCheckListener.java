package com.atguigu.gmall.payment.checklisten;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.MapMessage;
import java.util.Date;

@Component
public class DelayCheckListener {
    @Autowired
    PaymentService paymentService;
    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAYMENT_CHECK_QUEUE")
    public void checkResult(MapMessage mapMessage ){
        try{
            String out_trade_no = mapMessage.getString("out_trade_no");
            int count = mapMessage.getInt("count");
           PaymentInfo paymentInfo = paymentService.checkPaymentResult(out_trade_no);
            String status = paymentInfo.getPaymentStatus();
            if(count > 0) {
                if (status != null && (status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED"))) {
                    boolean b = paymentService.checkPaymentStatus(out_trade_no);
                    if (!b) {
                        PaymentInfo paymentInfoUpdate = new PaymentInfo();
                        paymentInfoUpdate.setPaymentStatus("已支付");
                        paymentInfoUpdate.setCallbackContent(paymentInfo.getCallbackContent());
                        paymentInfoUpdate.setOutTradeNo(out_trade_no);
                        paymentInfoUpdate.setAlipayTradeNo(paymentInfo.getAlipayTradeNo());
                        paymentInfoUpdate.setCallbackTime(new Date());
                        paymentService.updatePayment(paymentInfoUpdate);
                        paymentService.sendPaymentSuccess(paymentInfo.getOutTradeNo(), paymentInfo.getPaymentStatus(), paymentInfo.getAlipayTradeNo());

                    }
                } else {
                    System.out.println("正在进行第" + (6 - count) + "支付结果次检查，检查用户尚未付款成功，继续巡检");
                    paymentService.sendDelayPaymentCheck(out_trade_no, count - 1);

                }
            }else {
                System.out.println("支付结果次检查次数耗尽，支付未果。。。");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
