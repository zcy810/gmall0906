package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CatrController {
    @Reference
    SkuService skuService;

    @RequestMapping("addToCart")
    public String goCart(HttpServletRequest request, HttpServletResponse response, String skuId, int num) {
        String userId = "";
        SkuInfo skuInfo =skuService.getSkuBySkuId(skuId);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setSkuNum(num);
        cartInfo.setCartPrice(skuInfo.getPrice().multiply(new BigDecimal(num)));
        cartInfo.setUserId(userId);
        cartInfo.setIsChecked("1");
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());

        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isBlank(userId)) {
            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            cartInfoList = JSON.parseArray(listCartCookie, CartInfo.class);
            if (StringUtils.isBlank(listCartCookie)) {
                cartInfoList = new ArrayList<>();
                cartInfoList.add(cartInfo);
            }else{
                boolean isNewCart = is_new(cartInfo,cartInfoList);
                if(isNewCart){
                    cartInfoList.add(cartInfo);
                }else {
                    for (CartInfo info : cartInfoList) {
                        info.setSkuNum(cartInfo.getSkuNum()+info.getSkuNum());
                        info.setSkuPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                    }
                }
            }
            CookieUtil.setCookie(request,response,"listCartCookie",JSON.toJSONString(cartInfoList),1000*60*60*24,true);
        }


        return "redirect:http://cart.gmall.com:8086/success.html";
    }

    private boolean is_new(CartInfo cartInfo, List<CartInfo> cartInfoList) {
        boolean isNewCatr = true;
        for (CartInfo info : cartInfoList) {
            String skuId = info.getSkuId();
            if(skuId.equals(cartInfo.getSkuId())){
                return false;
            }
        }
        return isNewCatr;
    }
}
