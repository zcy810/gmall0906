package com.atguigu.gmall.manage.manageservice.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.service.ManageService;
import javafx.scene.media.SubtitleTrack;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ManageServiceImpl implements ManageService{
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseCatalog1> getAll() {
        return baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getLog1(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);

        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getLog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        return baseCatalog3Mapper.select(baseCatalog3);

    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return baseAttrInfoMapper.select(baseAttrInfo);
    }

    @Override
    public BaseAttrInfo getAttrInfo(String id) {
        BaseAttrInfo attrInfo = baseAttrInfoMapper.selectByPrimaryKey(id);

        System.out.println(attrInfo+"*************************");
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfo.getId());
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
        attrInfo.setAttrValueList(baseAttrValueList);
        return attrInfo;
    }

}
