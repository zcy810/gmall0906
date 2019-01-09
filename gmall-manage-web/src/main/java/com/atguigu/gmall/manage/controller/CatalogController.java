package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseCatalog1;

import com.atguigu.gmall.bean.BaseCatalog2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.atguigu.service.ManageService;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CatalogController {

    @Reference
    private ManageService manageService;
    @ResponseBody
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1Page(){

        List<BaseCatalog1> list =  manageService.getAll();
        return list;
    }
    @ResponseBody
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog1Page2(String catalog1Id){

        List<BaseCatalog2> list2 =  manageService.getLog1(catalog1Id);
        return list2;
    }
}
