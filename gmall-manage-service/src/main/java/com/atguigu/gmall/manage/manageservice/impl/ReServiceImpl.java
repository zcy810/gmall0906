package com.atguigu.gmall.manage.manageservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.ReComment;
import com.atguigu.gmall.manage.mapper.ReCommentMapper;
import com.atguigu.service.ReService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class ReServiceImpl implements ReService{

    @Autowired
    ReCommentMapper reCommentMapper;
    @Override
    public List<ReComment> getAllList(String skuId) {
        ReComment reComment = new ReComment();
        reComment.setSkuId(skuId);
        List<ReComment> select = reCommentMapper.select(reComment);
        return select;
    }
}
