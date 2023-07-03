package com.mingzhi.service;

import com.mingzhi.pojo.Items;
import com.mingzhi.pojo.ItemsImg;
import com.mingzhi.pojo.ItemsParam;
import com.mingzhi.pojo.ItemsSpec;

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

}
