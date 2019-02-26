package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotation.LoginRequired;
import com.atguigu.gmall.bean.CartInfo;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
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
    @Reference
    CartService cartService;




    @RequestMapping("updataNumCart")
    public String updataNumCart(CartInfo cartInfo) {
        System.out.println(cartInfo);
        return "";
    }

    @LoginRequired(isNeedLogin = false)
    @RequestMapping("checkCart")
    public String checkCart(HttpServletRequest request, CartInfo cartInfo, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfos = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            cartInfos = cartService.cartListFromCache(userId);
        } else {
            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            cartInfos = JSON.parseArray(listCartCookie, CartInfo.class);
        }
        for (CartInfo Info : cartInfos) {
            if (cartInfo.getSkuId().equals(Info.getSkuId())) {
                Info.setIsChecked(cartInfo.getIsChecked());
                if (StringUtils.isNotBlank(userId)) {
                    cartService.updateCart(Info);
                    cartService.flushCartCacheByUser(userId);
                } else {
                    CookieUtil.setCookie(request, response, "listCartCookie", JSON.toJSONString(cartInfos),60 * 60 * 24, true);
                }
            }
        }
        request.setAttribute("cartList", cartInfos);
        BigDecimal totalPrice = getTotalPrice(cartInfos);
        request.setAttribute("totalPrice", totalPrice);

        return "cartListInner";
    }

    @LoginRequired(isNeedLogin = false)
    @RequestMapping("cartList")
    public String goCartList(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        ;
        List<CartInfo> cartInfos = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            cartInfos = cartService.cartListFromCache(userId);
        } else {
            String listCartCookie = CookieUtil.getCookieValue(request, "listCartCookie", true);
            if (StringUtils.isNotBlank(listCartCookie)) {
                cartInfos = JSON.parseArray(listCartCookie, CartInfo.class);
            }
        }
        request.setAttribute("cartList", cartInfos);
        BigDecimal totalPrice = getTotalPrice(cartInfos);
        request.setAttribute("totalPrice", totalPrice);
        return "cartList";
    }

    private BigDecimal getTotalPrice(List<CartInfo> cartInfos) {
        BigDecimal total = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            if (cartInfo.getIsChecked().equals("1")) {
                total = total.add(cartInfo.getCartPrice());
            }
        }
        return total;
    }

    @LoginRequired(isNeedLogin = false)
    @RequestMapping("addToCart")
    public String goCart(HttpServletRequest request, HttpServletResponse response, String skuId, int num) {
        String userId = (String) request.getAttribute("userId");
        SkuInfo skuInfo = skuService.getSkuBySkuId(skuId);
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
            } else {
                boolean isNewCart = is_new(cartInfo, cartInfoList);
                if (isNewCart) {
                    cartInfoList.add(cartInfo);
                } else {
                    for (CartInfo info : cartInfoList) {
                        info.setSkuNum(cartInfo.getSkuNum() + info.getSkuNum());
                        info.setSkuPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                    }
                }
            }
            CookieUtil.setCookie(request, response, "listCartCookie", JSON.toJSONString(cartInfoList),  60 * 60 * 24, true);
        } else {
            CartInfo exists = new CartInfo();
            exists.setUserId(userId);
            exists.setSkuId(skuId);
            CartInfo cartInfoIfExist = cartService.exists(exists);

            if (cartInfoIfExist == null) {
                cartService.saveCart(cartInfo);
            } else {
                cartInfoIfExist.setSkuNum(cartInfo.getSkuNum() + cartInfoIfExist.getSkuNum());
                cartInfoIfExist.setCartPrice(cartInfoIfExist.getSkuPrice().multiply(new BigDecimal(cartInfoIfExist.getSkuNum())));
            }
            cartService.flushCartCacheByUser(userId);
        }


        return "redirect:http://cart.gmall.com:8086/success.html";
    }

    private boolean is_new(CartInfo cartInfo, List<CartInfo> cartInfoList) {
        boolean isNewCatr = true;
        for (CartInfo info : cartInfoList) {
            String skuId = info.getSkuId();
            if (skuId.equals(cartInfo.getSkuId())) {
                return false;
            }
        }
        return isNewCatr;
    }
}
