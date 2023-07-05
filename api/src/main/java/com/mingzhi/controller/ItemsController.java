package com.mingzhi.controller;

import com.mingzhi.enums.CommentLevel;
import com.mingzhi.pojo.Items;
import com.mingzhi.pojo.ItemsImg;
import com.mingzhi.pojo.ItemsParam;
import com.mingzhi.pojo.ItemsSpec;
import com.mingzhi.pojo.vo.CommentLevelCountsVO;
import com.mingzhi.pojo.vo.ItemInfoVO;
import com.mingzhi.pojo.vo.ShopCartVO;
import com.mingzhi.service.ItemService;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = {"商品信息展示"})
@RestController()
@RequestMapping("items")
public class ItemsController {
    final static Logger logger = LoggerFactory.getLogger(ItemsController.class);
    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public MingzhiJSONResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);
        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemImgList);
        itemInfoVO.setItemSpecList(itemSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return MingzhiJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级以及评价数", notes = "查询商品评价等级以及评价数", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public MingzhiJSONResult commentLevelCounts(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId
    ) {
        if (StringUtils.isBlank(itemId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        CommentLevelCountsVO commentLevelCountsVO = itemService.queryCommentCounts(itemId);
        return MingzhiJSONResult.ok(commentLevelCountsVO);
    }

    @ApiOperation(value = "查询商品评价", notes = "查询商品评价", httpMethod = "GET")
    @GetMapping("/comments")
    public MingzhiJSONResult comment(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "commentLevel", value = "评价等级", required = false, defaultValue = "1")
            @RequestParam Integer commentLevel,
            @ApiParam(name = "page", value = "第几页", required = false, defaultValue = "1")
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页条数", required = false, defaultValue = "10")
            @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(itemId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        PagedGridResult pagedGridResult = itemService.queryComments(itemId, commentLevel, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }


    @ApiOperation(value = "搜索商品", notes = "搜索商品", httpMethod = "GET")
    @GetMapping("/search")
    public MingzhiJSONResult searchItems(
            @ApiParam(name = "keywords", value = "商品关键字")
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序依据", required = false, defaultValue = "k")
            @RequestParam String sort,
            @ApiParam(name = "page", value = "第几页", required = false, defaultValue = "1")
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页条数", required = false, defaultValue = "10")
            @RequestParam Integer pageSize) {
        PagedGridResult pagedGridResult = itemService.searchItems(keywords, sort, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "搜索商品通过商品分类", notes = "搜索商品通过商品分类", httpMethod = "GET")
    @GetMapping("/catItems")
    public MingzhiJSONResult searchItemsByThirdCategory(
            @ApiParam(name = "catId", value = "商品关键字")
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序依据", required = false, defaultValue = "k")
            @RequestParam String sort,
            @ApiParam(name = "page", value = "第几页", required = false, defaultValue = "1")
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页条数", required = false, defaultValue = "10")
            @RequestParam Integer pageSize) {
        PagedGridResult pagedGridResult = itemService.searchItemsByThirdCategory(catId, sort, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }

    @ApiOperation(value = "根据商品规格ids查询商品数据", notes = "根据商品规格ids查询商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public MingzhiJSONResult queryItemsBySpecIds(
            @ApiParam(name = "itemSpecIds", value = "商品规格ids", required = true, example = "1001,1003,1005")
            @RequestParam String itemSpecIds
    ) {
        if (StringUtils.isBlank(itemSpecIds)) {
            return MingzhiJSONResult.ok();
        }
        List<ShopCartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);
        return MingzhiJSONResult.ok(list);
    }


}