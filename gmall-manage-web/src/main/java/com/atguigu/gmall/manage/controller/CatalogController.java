package com.atguigu.gmall.manage.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;

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
    }@ResponseBody
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog2Page3(String catalog2Id){

        List<BaseCatalog3> list3 =  manageService.getLog3(catalog2Id);
        return list3;
    }
    @ResponseBody
    @RequestMapping("getAttrList")
    public List<BaseAttrInfo> getAttrList1(String catalog3Id){

        return manageService.getAttrList(catalog3Id);
    }
    @ResponseBody
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getBaseAttrValue(String attrId){
        System.out.println("attrId=" + attrId);
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        return attrInfo.getAttrValueList();
    }
}
