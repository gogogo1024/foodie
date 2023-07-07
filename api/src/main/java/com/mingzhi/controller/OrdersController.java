package com.mingzhi.controller;

import com.mingzhi.enums.PayMethod;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.service.AddressService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Tag(name = "订单接口", description = "订单接口")
@RestController()
@ResponseBody()
@RequestMapping("orders")
public class OrdersController {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    private AddressService addressService;

    @Operation(summary = "用户提交订单", description = "用户提交订单", method = "POST")
    @PostMapping("/create")
    public MingzhiJSONResult queryAll(
            @Parameter(name = "submitOrderBO", description = "提交订单BO", required = true)
            @RequestBody SubmitOrderBO submitOrderBO) {
        if (!Objects.equals(submitOrderBO.getPayMethod(), PayMethod.WEIXIN.type) || !Objects.equals(submitOrderBO.getPayMethod(), PayMethod.ALIPAY.type)) {
            return MingzhiJSONResult.errorMsg("支付方式不支持");
        }
        System.out.println(submitOrderBO.toString());
        // 订单创建
        // 移除购物车中已结算商品
        // 调用支付中心，保存支付中心订单
        return MingzhiJSONResult.ok();
    }
}
