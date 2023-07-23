package com.mingzhi.controller;

import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.service.CarouselService;
import com.mingzhi.service.CategoryService;
import com.mingzhi.utils.JsonUtils;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.RedisOperator;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Tag(name = "购物车", description = "购物车相关接口")
@RestController()
@ResponseBody()
@RequestMapping("shopcart")
public class ShopController extends BaseController {
    final static Logger logger = LoggerFactory.getLogger(ShopController.class);
    @Autowired
    private CarouselService carouselService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;


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
        String shopCartJSONStr = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopCartBO> list = null;
        if (StringUtils.isNotBlank(shopCartJSONStr)) {
            list = JsonUtils.jsonToList(shopCartJSONStr, ShopCartBO.class);
            boolean isHaving = false;
            if (list != null) {
                for (ShopCartBO scb : list) {
                    String specId = scb.getSpecId();
                    // 判断购物车中是否存在已有商品，如果有对应商品需要叠加shopCartBO的buyCount
                    if (Objects.equals(specId, shopCartBO.getSpecId())) {
                        scb.setBuyCounts(scb.getBuyCounts() + shopCartBO.getBuyCounts());
                        isHaving = true;
                    }
                }
                if (!isHaving) {
                    list.add(shopCartBO);
                }
            }
        } else {
            // redis中没有购物车
            list = new ArrayList<>();
            // 直接添加到购物车中
            list.add(shopCartBO);
        }
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(list));
        System.out.println(shopCartBO);
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
        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        String shopCartJSONStr = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopCartJSONStr)) {
            // redis中已经有购物车了
            List<ShopCartBO> list = JsonUtils.jsonToList(shopCartJSONStr, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话则删除
            if (list != null) {
                for (ShopCartBO sc : list) {
                    String tmpSpecId = sc.getSpecId();
                    if (tmpSpecId.equals(itemSpecId)) {
                        list.remove(sc);
                        break;
                    }
                }
            }
            // 覆盖现有redis中的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(list));
        }

        return MingzhiJSONResult.ok();
    }

}
