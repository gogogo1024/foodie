package com.mingzhi.mapper;

import com.mingzhi.pojo.vo.CategoryVO;
import com.mingzhi.pojo.vo.NewItemsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CategoryMapperCustom {
    public List<CategoryVO> getSubCategoryList(int categoryId);

    // 自定义Map和xml中来对应
    public List<NewItemsVO> getMoreItemsList(@Param("paramsMap") Map<String, Object> map);


}