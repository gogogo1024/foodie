package com.mingzhi.controller;

import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.service.CarouselService;
import com.mingzhi.service.CategoryService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "购物车", tags = {"购物车相关接口"})
@RestController()
@RequestMapping("shopcart")
public class ShopController {
    final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public MingzhiJSONResult add(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "shopCartBO", value = "购物车VO", required = true)
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

    @ApiOperation(value = "刷新购物车数据", notes = "刷新购物车数据", httpMethod = "POST")
    @PostMapping("/refresh")
    public MingzhiJSONResult refreshShopcart(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "shopCartBO", value = "购物车VO", required = true)
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

    @ApiOperation(value = "从购物车移除商品", notes = "从购物车移除商品", httpMethod = "DELETE")
    @DeleteMapping("/del")
    public MingzhiJSONResult delete(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "itemSpecId", value = " 商品规格id", required = true)
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
