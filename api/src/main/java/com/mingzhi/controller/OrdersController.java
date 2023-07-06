package com.mingzhi.controller;

import com.mingzhi.enums.PayMethod;
import com.mingzhi.pojo.UserAddress;
import com.mingzhi.pojo.bo.AddressBO;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.service.AddressService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Api(value = "订单接口", tags = {"订单接口"})
@RestController()
@RequestMapping("orders")
public class OrdersController {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "用户提交订单", notes = "用户提交订单", httpMethod = "POST")
    @PostMapping("/create")
    public MingzhiJSONResult queryAll(
            @ApiParam(name = "submitOrderBO", value = "订单提交BO", required = true)
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
