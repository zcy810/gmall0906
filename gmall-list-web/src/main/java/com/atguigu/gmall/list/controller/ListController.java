package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.service.AttrService;
import com.atguigu.service.ListService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ListController {

    @Reference
    ListService listService;
    @Reference
    AttrService attrService;

    @RequestMapping("index")
    public String goIndex(){
        return "index";
    }
    @RequestMapping("list.html")
    public String list(SkuLsParam skuLsParam, ModelMap map){
        List<SkuLsInfo> skuLsInfoList =listService.list(skuLsParam);
        map.put("skuLsInfoList",skuLsInfoList);
        Set<String> valueIds = new HashSet<>();
        //去重操作
        for (SkuLsInfo skuLsInfo : skuLsInfoList) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                valueIds.add(valueId);
            }
        }
        //把结果用,分隔开
        String join = StringUtils.join(valueIds, ",");
        List<BaseAttrInfo> attrList = attrService.getAttrListByValueIds(join);

        //新建一个面包屑的集合
        List<AttrValueCrumb> attrValueCrumbs = new ArrayList<>();
        //从SkuLsInfo中获取ValueId
        String[] crumbValueIds = skuLsParam.getValueId();
        if(crumbValueIds != null && crumbValueIds.length > 0){
            for (String crumbValueId : crumbValueIds) {
                AttrValueCrumb attrValueCrumb = new AttrValueCrumb();
                //从attrList的子集合中获取`ValueName
                for (BaseAttrInfo baseAttrInfo : attrList) {
                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                    for (BaseAttrValue baseAttrValue : attrValueList) {
                        String id = baseAttrValue.getId();
                        if(id.equals(crumbValueId)){
                            //把获取到的ValueName复制到面包屑的valuename中
                            String valueName = baseAttrValue.getValueName();
                            attrValueCrumb.setValueName(valueName);
                String myUrlParam = getMyUrlParam(skuLsParam, crumbValueId);
                attrValueCrumb.setUrlParam(myUrlParam);
                attrValueCrumbs.add(attrValueCrumb);
                        }
                    }
                }
                //通过可变形参方法获取到urlparam并且复制到面包屑中
            }
        }

        String[] valueId = skuLsParam.getValueId();
        if(valueId != null && valueId.length > 0){
            Iterator<BaseAttrInfo> iterator = attrList.iterator();
            while (iterator.hasNext()){
                List<BaseAttrValue> attrValueList = iterator.next().getAttrValueList();
                for (BaseAttrValue baseAttrValue : attrValueList) {

                    String id = baseAttrValue.getId();
                    for (String sid : valueId) {
                        if(sid.equals(id)){
                            iterator.remove();
                        }
                    }
                }
            }
        }
        map.put("attrValueSelectedList",attrValueCrumbs);
        map.put("attrList",attrList);
        String urlParam = getMyUrlParam(skuLsParam);
        map.put("urlParam",urlParam);

        return "list";
    }

    private String getMyUrlParam(SkuLsParam skuLsParam,String...crumbValueId) {
        String urlParam = "";
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" +catalog3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" +keyword;
        }

        if(valueId!=null&&valueId.length>0){
            for (String id : valueId) {
                if(crumbValueId != null && !crumbValueId[0].equals(id)){

                urlParam = urlParam + "&valueId=" +id;
                }
            }
        }


        return urlParam;
    }
    private String getMyUrlParam(SkuLsParam skuLsParam) {
        String urlParam = "";
        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();

        if(StringUtils.isNotBlank(catalog3Id)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" +catalog3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" +keyword;
        }

        if(valueId!=null&&valueId.length>0){
            for (String id : valueId) {


                urlParam = urlParam + "&valueId=" +id;

            }
        }


        return urlParam;
    }


}
