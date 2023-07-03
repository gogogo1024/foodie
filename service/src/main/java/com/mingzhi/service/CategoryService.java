package com.mingzhi.service;

import com.mingzhi.pojo.Category;
import com.mingzhi.pojo.vo.CategoryVO;
import com.mingzhi.pojo.vo.NewItemsVO;

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

    /**
     * 获取对应分类下指定个数item
     *
     * @param category    分类
     * @param itemsLength item个数
     * @return 返回指定个数item
     */
    public List<NewItemsVO> getMoreItemsList(int category, int itemsLength);
}
