package com.atguigu.gmall.item.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.service.SkuService;
import com.atguigu.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ItemController {
    @Reference
    private SkuService skuService;
    @Reference
    private SpuService spuService;

    @RequestMapping("/{skuId}.html")
    public String getSkuInfo(@PathVariable("skuId") String skuId, HttpServletRequest request){
   SkuInfo skuInfo = skuService.getSkuInfo(skuId);
        String spuId = skuInfo.getSpuId();

        // 销售属性的集合
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = new ArrayList<>();

        spuSaleAttrListCheckBySku = spuService.getSpuSaleAttrListCheckBySku(spuId,skuId);
        request.setAttribute("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);
    request.setAttribute("skuInfo",skuInfo);


        return "item";
    }
}
