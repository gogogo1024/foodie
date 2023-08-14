package com.mingzhi.controller;

import com.mingzhi.service.ItemsESService;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.PagedGridResult;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController()
@RequestMapping("items")
@ResponseBody()
public class ItemsController {
    final static Logger logger = LoggerFactory.getLogger(ItemsController.class);
    @Autowired
    private ItemsESService itemsESService;

    @GetMapping("/hello")
    public Object hello() {

        logger.info("info:slf4j hello");
        logger.debug("debug:slf4j hello");

        return "ElasticSearch~";
    }

    @Operation(summary = "搜索商品", description = "搜索商品", method = "GET")
    @GetMapping("/search")
    public MingzhiJSONResult searchItems(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {
        if (StringUtils.isBlank(keywords)) {
            return MingzhiJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 20;
        }

        page--;
        PagedGridResult pagedGridResult = itemsESService.searchItems(keywords, sort, page, pageSize);
        return MingzhiJSONResult.ok(pagedGridResult);
    }
}
