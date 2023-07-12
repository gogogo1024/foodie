package com.mingzhi.controller;

import com.mingzhi.enums.PayMethod;
import com.mingzhi.enums.PayStatusEnum;
import com.mingzhi.pojo.PaymentOrders;
import com.mingzhi.pojo.bo.WeChatPayBO;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.pojo.vo.PaymentInfoVO;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.OrderService;
import com.mingzhi.service.PaymentOrderService;
import com.mingzhi.service.WechatPayService;
import com.mingzhi.utils.DateUtil;
import com.mingzhi.utils.MingzhiJSONResult;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Tag(name = "支付中心接口", description = "支付中心接口")
@RestController()
@ResponseBody()
@RequestMapping("payment")
public class PaymentController {
    final static Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private AddressService addressService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentOrderService paymentOrderService;

    @Autowired
    private WechatPayService wechatPayService;

    @Autowired
    private Environment env;
    @Autowired
    private RestTemplate restTemplate;

    @Operation(summary = "创建商户订单", description = "创建商户订单", method = "POST")
    @PostMapping("/createMerchantOrder")
    public MingzhiJSONResult createMerchantOrder(
            @RequestBody MerchantOrderVO merchantOrderVO
    ) {
        String merchantOrderId = merchantOrderVO.getMerchantOrderId();
        String merchantUserId = merchantOrderVO.getMerchantUserId();
        Integer amount = merchantOrderVO.getAmount();
        Integer payMethod = merchantOrderVO.getPayMethod();
        String returnUrl = merchantOrderVO.getReturnUrl();

        if (StringUtils.isBlank(merchantOrderId)) {
            return MingzhiJSONResult.errorMsg("参数[orderId]不能为空");
        }
        if (StringUtils.isBlank(merchantUserId)) {
            return MingzhiJSONResult.errorMsg("参数[userId]不能为空");
        }
        if (amount == null || amount < 1) {
            return MingzhiJSONResult.errorMsg("参数[realPayAmount]不能为空并且不能小于1");
        }
        if (payMethod == null) {
            return MingzhiJSONResult.errorMsg("参数[payMethod]不能为空并且不能小于1");
        }
        if (!payMethod.equals(PayMethod.WEIXIN.type) && !payMethod.equals(PayMethod.ALIPAY.type)) {
            return MingzhiJSONResult.errorMsg("参数[payMethod]目前只支持微信支付或支付宝支付");
        }
        if (StringUtils.isBlank(returnUrl)) {
            return MingzhiJSONResult.errorMsg("参数[returnUrl]不能为空");
        }

        String ss = env.getProperty("wechatpay.appId");
        System.out.println(ss);
        // 保存传来的商户订单信息
        boolean isSuccess = false;
        try {
            isSuccess = paymentOrderService.createPaymentOrder(merchantOrderVO);
        } catch (Exception e) {
            e.printStackTrace();
            MingzhiJSONResult.errorException(e.getMessage());
        }

        if (isSuccess) {
            return MingzhiJSONResult.ok("商户订单创建成功！");
        } else {
            return MingzhiJSONResult.errorMsg("商户订单创建失败，请重试...");
        }
    }

    @Operation(summary = "微信扫码支付", description = "微信扫码支付", method = "POST")
    @PostMapping("/getWXPayQRCode")
    public MingzhiJSONResult getWXPayQRCode(
            String merchantOrderId, String merchantUserId
    ) {
        PaymentOrders waitPayOrder = paymentOrderService.queryOrderByStatus(merchantOrderId, merchantUserId, PayStatusEnum.UNPAID.type);

        // 商品描述
        String description = "天天吃货-付款用户[" + merchantUserId + "]";
        // 商户订单号
        // 从redis中去获得这笔订单的微信支付二维码，如果订单状态没有支付没有就放入，这样的做法防止用户频繁刷新而调用微信接口
        // redis设置超时时间小于微信支付链接的有效时间（2小时）
        if (waitPayOrder != null) {
            // TODO redis获取waitPayOrder订单对应的支付二维码链接，
            //  如果存在直接组装,不存在时向微信发起支付请求获取二维码链接

            // 订单总金额，单位为分
            // 测试用 1分钱
            WeChatPayBO weChatPayBO = new WeChatPayBO();
            // weChatPayBO.setTotal(waitPayOrder.getAmount() + total_fee);
            weChatPayBO.setTotal(1);
            weChatPayBO.setOutTradeNo(merchantOrderId);
            weChatPayBO.setDescription(description);

            String qrCodeUrl = wechatPayService.getCodeUrl(weChatPayBO);

            PaymentInfoVO paymentInfoVO = new PaymentInfoVO();
            paymentInfoVO.setAmount(waitPayOrder.getAmount());
            paymentInfoVO.setMerchantOrderId(merchantOrderId);
            paymentInfoVO.setMerchantUserId(merchantUserId);
            paymentInfoVO.setQrCodeUrl(qrCodeUrl);

            return MingzhiJSONResult.ok(paymentInfoVO);
        } else {
            return MingzhiJSONResult.errorMsg("该订单不存在，或已经支付");
        }
    }

    @Operation(summary = "微信支付回调通知", description = "微信支付回调通知", method = "POST")
    @PostMapping("/notifyWechatPaid")
    public Integer notifyMerchantOrder(
            @Parameter(name = "orderId", description = "订单id", required = true)
            @RequestParam String orderId) {

        // 构造 RequestParam
        com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
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

/// 以支付通知回调为例，验签、解密并转换成 Transaction,transaction中包含了merchantOrderId,paidAmount
        // 解析transaction 查看是否支付，如果已支付直接处理，未支付则报错，并且返回给微信支付失败
        Transaction transaction = parser.parse(requestParam, Transaction.class);


        String merchantReturnUrl = paymentOrderService.updatePaymentOrderPaid("merchantOrderId", 1);
        logger.info("************* 支付成功(微信支付异步通知) - 时间: {} *************", DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));
        logger.info("* 商户订单号: {}", "merchantOrderId");
        logger.info("* 微信订单号: {}", "wxFlowId");
        logger.info("* 实际支付金额: {}", "paidAmount");
        logger.info("*****************************************************************************");
        // 通知电商主体服务端订单已支付
        MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
        requestEntity.add("merchantOrderId", "merchantOrderId");
        String httpStatus = restTemplate.postForObject(merchantReturnUrl, requestEntity, String.class);
        logger.info("*** 通知电商主体服务端后返回的状态码 httpStatus: {} ***", httpStatus);
        // TODO 通知微信支付中心，已经收到回调通知，不要再发消息来了
        return HttpStatus.OK.value();
    }
}
