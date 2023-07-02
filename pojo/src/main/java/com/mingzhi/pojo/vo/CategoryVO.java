package com.mingzhi.pojo.vo;

import java.util.List;

public class CategoryVO {
    private Integer id;
    private String name;
    private String type;
    private String fatherId;

    private List<SubCategoryVO> subCategoryList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public List<SubCategoryVO> getSubCategoryList() {
        return subCategoryList;
    }

    public void setSubCategoryList(List<SubCategoryVO> subCategoryList) {
        this.subCategoryList = subCategoryList;
    }
}
