package com.atguigu.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.annotation.LoginRequired;
import com.atguigu.gmall.bean.OrderInfo;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.conf.AlipayConfig;
import com.atguigu.service.OrderService;
import com.atguigu.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Controller
public class PaymentController {
    @Autowired
    AlipayClient alipayClient;
    @Reference
    OrderService orderService;
    @Autowired
    PaymentService paymentService;


    @LoginRequired(isNeedLogin = true)
    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String goToPay(HttpServletRequest request, String outTradeNo, BigDecimal totalAmount) {
        OrderInfo orderInfo = orderService.getOrderByOutTradeno(outTradeNo);
        String skuName = orderInfo.getOrderDetailList().get(0).getSkuName();

        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        alipayTradePagePayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayTradePagePayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("out_trade_no", outTradeNo);
        requestMap.put("product_code", "FAST_INSTANT_TRADE_PAY");
        requestMap.put("total_amount", "0.01");
        requestMap.put("subject", skuName);
        alipayTradePagePayRequest.setBizContent(JSON.toJSONString(requestMap));
        String form = "";
        try {
            form = alipayClient.pageExecute(alipayTradePagePayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(form);
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setTotalAmount(totalAmount);
        paymentInfo.setSubject(skuName);
        paymentInfo.setCreateTime(new Date());
        paymentService.save(paymentInfo);
        paymentService.sendDelayPaymentCheck(outTradeNo,5);
        return form;
    }

    @LoginRequired(isNeedLogin = true)
    @RequestMapping("paymentIndex")
    public String paymentIndex(HttpServletRequest request, String outTradeNo, BigDecimal totalAmount, ModelMap map) {
        String userId = (String) request.getAttribute("userId");

        map.put("outTradeNo", outTradeNo);
        map.put("totalAmount", totalAmount);

        return "paymentindex";
    }
    @RequestMapping("/alipay/callback/return")
    public String callback(HttpServletRequest request,Map<String,String> paramsMap){
        //map是异步签名验证用的
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        String sign = request.getParameter("sign");
        try {
            boolean b = AlipaySignature.rsaCheckV1(paramsMap,AlipayConfig.alipay_public_key,AlipayConfig.charset,AlipayConfig.sign_type);

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        boolean b = paymentService.checkPaymentStatus(out_trade_no);
        if(!b) {
            PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.setPaymentStatus("已支付");
            paymentInfo.setCallbackContent(request.getQueryString());
            paymentInfo.setOutTradeNo(out_trade_no);
            paymentInfo.setAlipayTradeNo(trade_no);
            paymentInfo.setCallbackTime(new Date());

        paymentService.sendPaymentSuccess(paymentInfo.getOutTradeNo(),paymentInfo.getPaymentStatus(),trade_no);

        paymentService.updatePayment(paymentInfo);
        }
        return "finish";
    }
}
