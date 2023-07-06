package com.mingzhi.service;

import com.mingzhi.pojo.Items;
import com.mingzhi.pojo.ItemsImg;
import com.mingzhi.pojo.ItemsParam;
import com.mingzhi.pojo.ItemsSpec;
import com.mingzhi.pojo.vo.CommentLevelCountsVO;
import com.mingzhi.pojo.vo.ItemCommentVO;
import com.mingzhi.pojo.vo.ItemSpecVO;
import com.mingzhi.pojo.vo.ShopCartVO;
import com.mingzhi.utils.PagedGridResult;

import java.util.List;

public interface ItemService {
    /**
     * 根据商品id查询详情
     *
     * @param itemId 商品id
     * @return 商品详情
     */
    public Items queryItemById(String itemId);


    /**
     * 根据商品id查询商品图片列表
     *
     * @param itemId 商品id
     * @return 商品图片列表
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品id查询商品规格
     *
     * @param itemId 商品id
     * @return 商品规格
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品id查询商品参数
     *
     * @param itemId 商品id
     * @return 商品参数
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品评论数
     *
     * @param itemId 商品id
     * @return 商品评论数
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据商品id，评价等级查询商品评论
     *
     * @param itemId       商品id
     * @param commentLevel 评价等级
     * @param page         第几页
     * @param pageSize     每页多少条
     * @return 商品评论列表
     */
    public PagedGridResult queryComments(String itemId, Integer commentLevel, Integer page, Integer pageSize);

    /**
     * 根据商品关键字，排序方式查询商品列表
     *
     * @param keywords 搜索关键字
     * @param sort     排序方式
     * @param page     第几页
     * @param pageSize 每页多少条
     * @return 关键字对应商品列表
     */
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

    /**
     * 根据商品id,排序方式查询商品列表
     *
     * @param catId    商品id
     * @param sort     排序方式
     * @param page     第几页
     * @param pageSize 每页多少条
     * @return 商品评论列表
     */
    public PagedGridResult searchItemsByThirdCategory(Integer catId, String sort, Integer page, Integer pageSize);


    /**
     * 根据商品规格ids，查询购物车商品列表
     *
     * @param specIds 商品规格ids
     * @return 购物车商品列表
     */
    public List<ShopCartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id，查询商品规格信息列表
     *
     * @param specIds 商品规格ids
     * @return 商品规格信息列表
     */
    public List<ItemSpecVO> queryItemsSpecBySpecIds(String specIds);

}