package com.atguigu.gmall.manage.manageservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.ReComment;
import com.atguigu.gmall.bean.ReReply;
import com.atguigu.gmall.manage.mapper.ReCommentMapper;
import com.atguigu.gmall.manage.mapper.ReReplyMapper;
import com.atguigu.service.ReService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class ReServiceImpl implements ReService{

    @Autowired
    ReCommentMapper reCommentMapper;
    @Autowired
    ReReplyMapper reReplyMapper;
    @Override
    public List<ReComment> getAllList(String skuId) {
        ReComment reComment = new ReComment();
        reComment.setSkuId(skuId);
        List<ReComment> select = reCommentMapper.select(reComment);
        return select;
    }

    @Override
    public ReComment getReCommentById(String id) {
        return reCommentMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ReReply>  getReRpplyByCommentId(String commentId) {
        ReReply reReply = new ReReply();
        reReply.setCommentId(commentId);
        List<ReReply> reReplyList = reReplyMapper.select(reReply);
        return reReplyList;
    }
}
