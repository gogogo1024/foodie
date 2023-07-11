package com.mingzhi.controller;

import com.mingzhi.enums.OrderStatusEnum;
import com.mingzhi.enums.PayMethod;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.pojo.vo.OrderVO;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.OrderService;
import com.mingzhi.utils.MingzhiJSONResult;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam.Builder;
import com.wechat.pay.java.service.payments.model.Transaction;
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
    public static String notifyUrl = "https://localhost:8088/orders/notifyWechatPaid";
    @Autowired
    private AddressService addressService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    private com.wechat.pay.java.core.notification.RequestParam WechatRequest;


    @Operation(summary = "用户提交订单", description = "用户提交订单", method = "POST")
    @PostMapping("/create")
    public MingzhiJSONResult create(
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
                "http://127.0.0.1:8088/payment/createMerchantOrder",
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

        // 构造 RequestParam
        com.wechat.pay.java.core.notification.RequestParam requestParam = new Builder()
                .serialNumber("wechatPayCertificateSerialNumber")
                .nonce("nonce")
                .signature("signature")
                .timestamp("timestamp")
                .body("requestBody")
                .build();

/// 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
/// 没有的话，则构造一个
        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId("merchantId")
                .privateKeyFromPath("privateKeyPath")
                .merchantSerialNumber("merchantSerialNumber")
                .apiV3Key("apiV3key")
                .build();

/// 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(config);

/// 以支付通知回调为例，验签、解密并转换成 Transaction
        Transaction transaction = parser.parse(requestParam, Transaction.class);

        orderService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

}
