package com.mingzhi.mapper;


import com.mingzhi.pojo.vo.ItemCommentVO;
import com.mingzhi.pojo.vo.SearchItemsVO;
import com.mingzhi.pojo.vo.ShopCartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ItemsMapperCustom {
    public List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItems(@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItemsByThirdCategory(@Param("paramsMap") Map<String, Object> map);

    public List<ShopCartVO> queryItemsBySpecIds(@Param("paramsList") List specIdsList);


}