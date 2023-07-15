package com.mingzhi.controller;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品接口", description = "商品信息展示")
@RestController()
@ResponseBody()
@RequestMapping("items")
public class ItemsController {
    final static Logger logger = LoggerFactory.getLogger(ItemsController.class);
    @Autowired
    private ItemService itemService;

    @Operation(summary = "查询商品详情", description = "查询商品详情", method = "GET")
    @GetMapping("/info/{itemId}")
    public MingzhiJSONResult info(
            @Parameter(name = "itemId", required = true)
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

    @Operation(summary = "查询商品评价等级以及评价数", description = "查询商品评价等级以及评价数", method = "GET")
    @GetMapping("/commentLevel")
    public MingzhiJSONResult commentLevelCounts(
            @Parameter(name = "itemId", required = true)
            @RequestParam String itemId
    ) {
        if (StringUtils.isBlank(itemId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        CommentLevelCountsVO commentLevelCountsVO = itemService.queryCommentCounts(itemId);
        return MingzhiJSONResult.ok(commentLevelCountsVO);
    }

    @Operation(summary = "查询商品评价", description = "查询商品评价", method = "GET")
    @GetMapping("/comments")
    public MingzhiJSONResult comment(
            @Parameter(name = "itemId", required = true)
            @RequestParam String itemId,
            @Parameter(name = "commentLevel", example = "1")
            @RequestParam(required = false) Integer commentLevel,
            @Parameter(name = "page", required = false, example = "1")
            @RequestParam Integer page,
            @Parameter(name = "pageSize", required = false, example = "10")
            @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(itemId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        PagedGridResult pagedGridResult = itemService.queryComments(itemId, commentLevel, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }


    @Operation(summary = "搜索商品", description = "搜索商品", method = "GET")
    @GetMapping("/search")
    public MingzhiJSONResult searchItems(
            @Parameter(name = "keywords")
            @RequestParam String keywords,
            @Parameter(name = "sort", required = false, example = "k")
            @RequestParam String sort,
            @Parameter(name = "page", required = false, example = "1")
            @RequestParam Integer page,
            @Parameter(name = "pageSize", required = false, example = "10")
            @RequestParam Integer pageSize) {
        PagedGridResult pagedGridResult = itemService.searchItems(keywords, sort, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }

    @Operation(summary = "搜索商品通过商品分类", description = "搜索商品通过商品分类", method = "GET")
    @GetMapping("/catItems")
    public MingzhiJSONResult searchItemsByThirdCategory(
            @Parameter(name = "catId")
            @RequestParam Integer catId,
            @Parameter(name = "sort", required = false, example = "k")
            @RequestParam String sort,
            @Parameter(name = "page", required = false, example = "1")
            @RequestParam Integer page,
            @Parameter(name = "pageSize", required = false, example = "10")
            @RequestParam Integer pageSize) {
        PagedGridResult pagedGridResult = itemService.searchItemsByThirdCategory(catId, sort, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }

    @Operation(summary = "根据商品规格ids查询商品数据", description = "根据商品规格ids查询商品数据", method = "GET")
    @GetMapping("/refresh")
    public MingzhiJSONResult queryItemsBySpecIds(
            @Parameter(name = "itemSpecIds", required = true, example = "1001,1003,1005")
            @RequestParam String itemSpecIds
    ) {
        if (StringUtils.isBlank(itemSpecIds)) {
            return MingzhiJSONResult.ok();
        }
        List<ShopCartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);
        return MingzhiJSONResult.ok(list);
    }


}
