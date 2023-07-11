package com.mingzhi.service.impl;

import com.mingzhi.pojo.bo.WeChatPayBO;
import com.mingzhi.service.WechatPayService;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 微信支付
 */
@Service
public class WechatPayServiceImpl implements WechatPayService {
    /**
     * 商户号
     */
    public static String merchantId = "";
    /**
     * 商户API私钥路径
     */
    public static String privateKeyPath = "";
    /**
     * 商户证书序列号
     */
    public static String merchantSerialNumber = "";
    /**
     * 商户APIV3密钥
     */
    public static String apiV3key = "";

    public static String notifyUrl = "https://123.com/orders/notifyWechatPaid";
    public static String appId = "appId";
    public static String wechatPayCertificatePath = "";

    public static NativePayService service;

    /**
     * Native支付预下单
     */
    public static PrepayResponse prepay(WeChatPayBO weChatPayBO) {
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(weChatPayBO.getTotal());
        request.setAmount(amount);
        request.setAppid(appId);
        request.setMchid(merchantId);
        request.setDescription(weChatPayBO.getDescription());
        request.setNotifyUrl(notifyUrl);
        request.setOutTradeNo(weChatPayBO.getOutTradeNo());
        return service.prepay(request);
    }

    /**
     * 关闭订单
     */
    public static void closeOrder() {
        CloseOrderRequest request = new CloseOrderRequest();
        service.closeOrder(request);
    }

    /**
     * 微信支付订单号查询订单
     */
    public static Transaction queryOrderById() {

        QueryOrderByIdRequest request = new QueryOrderByIdRequest();
        return service.queryOrderById(request);
    }

    /**
     * 商户订单号查询订单
     */
    public static Transaction queryOrderByOutTradeNo() {

        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        return service.queryOrderByOutTradeNo(request);
    }

    /**
     * 获取微信Native支付二维码url
     *
     * @param weChatPayBO 微信支付BO
     * @return 二维码url
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String getCodeUrl(WeChatPayBO weChatPayBO) {
        Config config =
                new RSAAutoCertificateConfig.Builder()
                        .merchantId(merchantId)
                        .privateKeyFromPath(privateKeyPath)
                        .merchantSerialNumber(merchantSerialNumber)
                        .apiV3Key(apiV3key)
                        .build();
        // 构建service
        service = new NativePayService.Builder().config(config).build();
        // 调用下单方法，得到应答
        PrepayResponse response = prepay(weChatPayBO);
        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
        String codeUrl = response.getCodeUrl();
        System.out.println(codeUrl);
        try {
            closeOrder();
        } catch (HttpException e) { // 发送HTTP请求失败
            // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义
        } catch (MalformedMessageException e) { // 服务返回成功，返回体类型不合法，或者解析返回体失败
            // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义
        }
        return codeUrl;
    }

}

