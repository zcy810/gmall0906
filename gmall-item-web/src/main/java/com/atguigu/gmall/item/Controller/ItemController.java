package com.atguigu.gmall.item.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Controller
public class ItemController {
    @Reference
    private SkuService skuService;
    @Reference
    private SpuService spuService;
    @Reference
    private ReService reService;
    @Reference
    private UserService userService;


   /* @RequestMapping("getComList")
    public List<ReComment> getComList(String skuId, Model model,HttpServletRequest request){
        List<ReComment> reList = reService.getAllList(skuId);
        //request.setAttribute("reList",reList);
       model.addAttribute("reList",reList);
        return reList;

    }*/

   @RequestMapping("getApply/{commentId}")
   public String goApply(@PathVariable("commentId") String commentId,HttpServletRequest request){
        ReComment reComment = reService.getReCommentById(commentId);
        //reComment为null!!!
        List<ReReply> reReplyList = reService.getReRpplyByCommentId(commentId);
        request.setAttribute("reComment",reComment);
        request.setAttribute("reReplyList",reReplyList);
       return "reReply";
   }


    @RequestMapping("/{skuId}.html")
    public String getSkuInfo(@PathVariable("skuId") String skuId, HttpServletRequest request){
   SkuInfo skuInfo = skuService.getSkuInfo(skuId,request.getRemoteAddr());
        String spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrListCheckBySku = new ArrayList<>();

        spuSaleAttrListCheckBySku = spuService.getSpuSaleAttrListCheckBySku(spuId,skuId);


        request.setAttribute("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);
    request.setAttribute("skuInfo",skuInfo);
        Map<String,String> skuMap = new HashMap<>();
        List<SkuInfo> skuInfos = skuService.getSkuSaleAttrValueListBySpu(spuId);
        for (SkuInfo info : skuInfos) {
            String v = info.getId();
            String k = "";
            List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                String saleAttrValueId = skuSaleAttrValue.getSaleAttrValueId();
                k = k + "|" + saleAttrValueId;

            }
                skuMap.put(k,v);
        }
        request.setAttribute("skuMap", JSON.toJSONString(skuMap));
        List<ReComment> reList = reService.getAllList(skuId);
        //request.setAttribute("reList",reList);
        request.setAttribute("reList",reList);

        return "item";
    }
}
