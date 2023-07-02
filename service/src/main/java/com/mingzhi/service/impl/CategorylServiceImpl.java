package com.mingzhi.service.impl;

import com.mingzhi.mapper.CarouselMapper;
import com.mingzhi.mapper.CategoryMapper;
import com.mingzhi.mapper.CategoryMapperCustom;
import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.Category;
import com.mingzhi.pojo.vo.CategoryVO;
import com.mingzhi.service.CarouselService;
import com.mingzhi.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategorylServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryMapperCustom categoryMapperCustom;

    /**
     * 查询对应分级的分类列表
     *
     * @return 分类列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Category> queryLevelCategory(int type) {
        Example example = new Example(Category.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type", type);
        return categoryMapper.selectByExample(example);
    }

    /**
     * 查询分类下的子类列表
     *
     * @param categoryId 分类id
     * @return 子类列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<CategoryVO> getSubCategoryList(int categoryId) {
        return categoryMapperCustom.getSubCategoryList(categoryId);
    }
}
