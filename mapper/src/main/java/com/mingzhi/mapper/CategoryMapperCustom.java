package com.mingzhi.mapper;

import com.mingzhi.my.mapper.MyMapper;
import com.mingzhi.pojo.Category;
import com.mingzhi.pojo.vo.CategoryVO;

import java.util.List;

public interface CategoryMapperCustom {
    public List<CategoryVO> getSubCategoryList(int categoryId);


}