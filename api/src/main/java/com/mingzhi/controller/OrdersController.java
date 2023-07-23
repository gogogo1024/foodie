package com.mingzhi.controller;

import com.mingzhi.enums.OrderStatusEnum;
import com.mingzhi.enums.PayMethod;
import com.mingzhi.pojo.OrderStatus;
import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.pojo.vo.OrderVO;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.OrderService;
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
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;


@Tag(name = "订单接口", description = "订单接口")
@RestController()
@ResponseBody()
@RequestMapping("orders")
public class OrdersController extends BaseController {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);
    public static String returnUrl = "https://localhost:8088/orders/notifyMerchantOrderPaid";
    @Autowired
    private AddressService addressService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private OrderService orderService;
    private com.wechat.pay.java.core.notification.RequestParam WechatRequest;

    @Autowired
    private RedisOperator redisOperator;

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
        String shopCartStr = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopCartStr)) {
            return MingzhiJSONResult.errorMsg("购物数据不正确");
        }
        List<ShopCartBO> list = JsonUtils.jsonToList(shopCartStr, ShopCartBO.class);


        // 订单创建
        OrderVO orderVO = orderService.creatOrder(submitOrderBO, list);
        String orderId = orderVO.getOrderId();
        MerchantOrderVO merchantOrderVO = orderVO.getMerchantOrderVO();
        merchantOrderVO.setReturnUrl(returnUrl);
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

    @Operation(summary = "支付中心通知电商主体服务端订单已支付", description = "支付中心通知电商主体服务端订单已支付", method = "POST")
    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @Operation(summary = "获取订单信息", description = "获取订单信息", method = "POST")
    @PostMapping("getPaidOrderInfo")
    public MingzhiJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderInfo(orderId);
        return MingzhiJSONResult.ok(orderStatus);
    }
}
