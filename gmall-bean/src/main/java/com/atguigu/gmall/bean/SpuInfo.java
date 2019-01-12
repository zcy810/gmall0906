package com.atguigu.gmall.bean;

import java.io.Serializable;

public class SpuInfo implements Serializable{
    private String id;
    private String spuName;
    private String description;
    private String catalog3Id;
    private String tmId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(String catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getTmId() {
        return tmId;
    }

    public void setTmId(String tmId) {
        this.tmId = tmId;
    }

    public SpuInfo(String id, String spuName, String description, String catalog3Id, String tmId) {

        this.id = id;
        this.spuName = spuName;
        this.description = description;
        this.catalog3Id = catalog3Id;
        this.tmId = tmId;
    }

    public SpuInfo() {

    }
}
