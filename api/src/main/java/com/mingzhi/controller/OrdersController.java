package com.mingzhi.controller;

import com.mingzhi.enums.OrderStatusEnum;
import com.mingzhi.enums.PayMethod;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.pojo.vo.OrderVO;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.OrderService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Tag(name = "订单接口", description = "订单接口")
@RestController()
@ResponseBody()
@RequestMapping("orders")
public class OrdersController {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    private AddressService addressService;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    public static String notifyUrl = "https://123.com/orders/notifyWechatPaid";


    @Operation(summary = "用户提交订单", description = "用户提交订单", method = "POST")
    @PostMapping("/create")
    public MingzhiJSONResult queryAll(
            @Parameter(name = "submitOrderBO", description = "提交订单BO", required = true)
            @RequestBody SubmitOrderBO submitOrderBO,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (!(Objects.equals(submitOrderBO.getPayMethod(), PayMethod.WEIXIN.type) || Objects.equals(submitOrderBO.getPayMethod(), PayMethod.ALIPAY.type))) {
            return MingzhiJSONResult.errorMsg("支付方式不支持");
        }
        // 订单创建
        OrderVO orderVO = orderService.creatOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();
        MerchantOrderVO merchantOrderVO = orderVO.getMerchantOrderVO();
        merchantOrderVO.setReturnUrl(notifyUrl);
        // 目前都为1分钱暂时
        merchantOrderVO.setAmount(1);
        // 移除购物车中已结算商品
        // TODO redis中移除购物车中已结算商品
//        CookieUtils.setCookie(request, response, "shopcart", "", true);


        // 调用支付中心，保存支付中心订单
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MerchantOrderVO> entity = new HttpEntity<>(merchantOrderVO, httpHeaders);
        ResponseEntity<MingzhiJSONResult> responseEntity = restTemplate.postForEntity(
                "http://localhost:8088/payment/createMerchantOrder",
                entity,
                MingzhiJSONResult.class
        );
        MingzhiJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult != null && paymentResult.getStatus() != 200) {
            return MingzhiJSONResult.errorMsg("支付中心订单创建失败");
        }

        return MingzhiJSONResult.ok(orderId);
    }

    @Operation(summary = "微信支付回调通知", description = "微信支付回调通知", method = "POST")
    @PostMapping("/notifyWechatPaid")
    public Integer notifyMerchantOrder(
            @Parameter(name = "orderId", description = "订单id", required = true)
            @RequestParam String orderId) {

        orderService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
        // 订单创建
//        String orderId = orderService.creatOrder(submitOrderBO);
//
//        // 移除购物车中已结算商品
//        // TODO redis中移除购物车中已结算商品
////        CookieUtils.setCookie(request, response, "shopcart", "", true);
//
//
//        // 调用支付中心，保存支付中心订单
    }
}
