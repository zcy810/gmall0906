package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.service.SkuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SkuController {
    @Reference
    private SkuService skuService;

    @ResponseBody
    @RequestMapping("skuInfoListBySpu")
    public List<SkuInfo> skuInfoListBySpu(String spuId){
        List<SkuInfo> list = skuService.selectAll();
        return list;
    }
    @RequestMapping("saveSku")
    @ResponseBody
    public String saveSku(SkuInfo skuInfo){
        skuService.setSkuInfo(skuInfo);
        return "success";
    }
}
