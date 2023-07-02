package com.mingzhi.service;

import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.Category;
import com.mingzhi.pojo.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    /**
     * 查询对应分级的分类列表
     *
     * @return 分类列表
     */
    public List<Category> queryLevelCategory(int type);

    /**
     * 查询分类下的子类列表
     *
     * @param categoryId 分类id
     * @return 子类列表
     */
    public List<CategoryVO> getSubCategoryList(int categoryId);
}
