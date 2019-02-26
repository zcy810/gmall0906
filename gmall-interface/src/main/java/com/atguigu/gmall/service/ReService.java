package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.ReComment;
import com.atguigu.gmall.bean.ReReply;

import java.util.List;

public interface ReService {
    List<ReComment> getAllList(String skuId);

    ReComment getReCommentById(String d);

    List<ReReply>  getReRpplyByCommentId(String commentId);
}
