package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class spuController {

    @Reference
    private SpuService spuService;
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> getskuInfoList(String catalog3Id){
        List<SpuInfo> list = spuService.getSpuList(catalog3Id);
        return  list;
    }

}
