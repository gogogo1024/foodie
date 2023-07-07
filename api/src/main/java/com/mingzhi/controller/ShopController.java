package com.mingzhi.controller;

import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.service.CarouselService;
import com.mingzhi.service.CategoryService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "购物车", description = "购物车相关接口")
@RestController()
@ResponseBody()
@RequestMapping("shopcart")
public class ShopController {
    final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "添加商品到购物车", description = "添加商品到购物车", method = "POST")
    @PostMapping("/add")
    public MingzhiJSONResult add(
            @Parameter(name = "userId", required = true)
            @RequestParam String userId,
            @Parameter(name = "shopCartBO", required = true)
            @RequestBody ShopCartBO shopCartBO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // TODO swagger 在POST下url参数userId拼接有问题
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        System.out.println(shopCartBO);
        //TODO 用户已登录，商品购物车同步到redis分布式缓存
        return MingzhiJSONResult.ok();
    }

    @Operation(summary = "刷新购物车数据", description = "刷新购物车数据", method = "POST")
    @PostMapping("/refresh")
    public MingzhiJSONResult refreshShopcart(
            @Parameter(name = "userId", required = true)
            @RequestParam String userId,
            @Parameter(name = "shopCartBO", required = true)
            @RequestBody ShopCartBO shopCartBO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // TODO swagger 在POST下url参数userId拼接有问题
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        System.out.println(shopCartBO);
        //TODO 用户已登录，商品购物车同步到redis分布式缓存
        return MingzhiJSONResult.ok();
    }

    @Operation(summary = "从购物车移除商品", description = "从购物车移除商品", method = "DELETE")
    @DeleteMapping("/del")
    public MingzhiJSONResult delete(
            @Parameter(name = "userId", required = true)
            @RequestParam String userId,
            @Parameter(name = "itemSpecId", required = true)
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        //TODO 用户已登录，用户移除商品购物车商品，需要同步删除后端购物车商品
        return MingzhiJSONResult.ok();
    }

}
