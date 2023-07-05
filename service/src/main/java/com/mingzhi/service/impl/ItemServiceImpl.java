package com.mingzhi.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mingzhi.enums.CommentLevel;
import com.mingzhi.mapper.*;
import com.mingzhi.pojo.*;
import com.mingzhi.pojo.vo.CommentLevelCountsVO;
import com.mingzhi.pojo.vo.ItemCommentVO;
import com.mingzhi.pojo.vo.SearchItemsVO;
import com.mingzhi.pojo.vo.ShopCartVO;
import com.mingzhi.service.ItemService;
import com.mingzhi.utils.DesensitizationUtil;
import com.mingzhi.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemsMapper itemsMapper;

    @Autowired
    private ItemsImgMapper itemsImgMapper;

    @Autowired
    private ItemsParamMapper itemsParamMapper;

    @Autowired
    private ItemsSpecMapper itemsSpecMapper;

    @Autowired
    private ItemsMapperCustom itemsMapperCustom;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;


    /**
     * 根据商品id查询详情
     *
     * @param itemId 商品id
     * @return 商品详情
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    /**
     * 根据商品id查询商品图片列表
     *
     * @param itemId 商品id
     * @return 商品图片列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example itemsImgExp = new Example(ItemsImg.class);
        Example.Criteria criteria = itemsImgExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsImgMapper.selectByExample(itemsImgExp);
    }

    /**
     * 根据商品id查询商品规格
     *
     * @param itemId 商品id
     * @return 商品规格
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example itemSpecExp = new Example(ItemsSpec.class);
        Example.Criteria criteria = itemSpecExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsSpecMapper.selectByExample(itemSpecExp);
    }

    /**
     * 根据商品id查询商品参数
     *
     * @param itemId 商品id
     * @return 商品参数
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example itemParamExp = new Example(ItemsParam.class);
        Example.Criteria criteria = itemParamExp.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsParamMapper.selectOneByExample(itemParamExp);
    }

    /**
     * 根据商品id查询商品评论数
     *
     * @param itemId 商品id
     * @return 商品评论数
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        Integer goodCounts = getCommentCounts(itemId, CommentLevel.GOOD.type);
        Integer normalCounts = getCommentCounts(itemId, CommentLevel.NORMAL.type);
        Integer badCounts = getCommentCounts(itemId, CommentLevel.BAD.type);
        Integer totalCounts = goodCounts + normalCounts + badCounts;
        CommentLevelCountsVO commentLevelCountsVO = new CommentLevelCountsVO();
        commentLevelCountsVO.setGoodCounts(goodCounts);
        commentLevelCountsVO.setNormalCounts(normalCounts);
        commentLevelCountsVO.setBadCounts(badCounts);
        commentLevelCountsVO.setTotalCounts(totalCounts);
        return commentLevelCountsVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer getCommentCounts(String itemId, Integer level) {
        ItemsComments condition = new ItemsComments();
        condition.setItemId(itemId);
        condition.setCommentLevel(level);
        return itemsCommentsMapper.selectCount(condition);
    }

    /**
     * 根据商品id，评价等级查询商品评论
     *
     * @param itemId       商品id
     * @param commentLevel 评价等级
     * @param page         第几页
     * @param pageSize     每页多少条
     * @return 商品评论列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryComments(String itemId, Integer commentLevel, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", itemId);
        map.put("commentLevel", commentLevel);
        PageHelper.startPage(page, pageSize);
        List<ItemCommentVO> list = itemsMapperCustom.queryItemComments(map);
        // 姓名脱敏
        for (ItemCommentVO vo : list) {
            vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
        }
        PagedGridResult pagedGridResult = new PagedGridResult();
        return pagedGridResult.setterPagedGrid(list, page);
    }

    /**
     * 根据商品id，排序方式查询商品列表
     *
     * @param keywords 搜索关键字
     * @param sort     排序方式
     * @param page     第几页
     * @param pageSize 每页多少条
     * @return 关键字对应商品列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("keywords", keywords);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItems(map);
        PagedGridResult pagedGridResult = new PagedGridResult();
        return pagedGridResult.setterPagedGrid(list, page);
    }

    /**
     * 根据商品id,排序方式查询商品列表
     *
     * @param catId    商品id
     * @param sort     排序方式
     * @param page     第几页
     * @param pageSize 每页多少条
     * @return 商品评论列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult searchItemsByThirdCategory(Integer catId, String sort, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("catId", catId);
        map.put("sort", sort);
        PageHelper.startPage(page, pageSize);
        List<SearchItemsVO> list = itemsMapperCustom.searchItemsByThirdCategory(map);
        PagedGridResult pagedGridResult = new PagedGridResult();
        return pagedGridResult.setterPagedGrid(list, page);
    }

    /**
     * 根据商品规格ids，查询购物车商品列表
     *
     * @param specIds 商品规格ids
     * @return 购物车商品列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopCartVO> queryItemsBySpecIds(String specIds) {
        String[] ids = specIds.split(",");
        List<String> specIdsList = new ArrayList<>();
        Collections.addAll(specIdsList, ids);
        return itemsMapperCustom.queryItemsBySpecIds(specIdsList);
    }

}
