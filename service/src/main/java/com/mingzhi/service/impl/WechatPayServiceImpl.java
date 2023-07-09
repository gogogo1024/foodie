package com.mingzhi.service.impl;

import com.mingzhi.pojo.bo.WeChatPayBO;
import com.mingzhi.service.WechatPayService;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
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
        NativePayService service = new NativePayService.Builder().config(config).build();
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(weChatPayBO.getTotal());
        request.setAmount(amount);
        request.setAppid(weChatPayBO.getAppId());
        request.setMchid(weChatPayBO.getMchid());
        request.setDescription(weChatPayBO.getDescription());
        request.setNotifyUrl(weChatPayBO.getNotifyUrl());
        request.setOutTradeNo(weChatPayBO.getOutTradeNo());
        // 调用下单方法，得到应答
        PrepayResponse response = service.prepay(request);
        // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
        String codeUrl = response.getCodeUrl();
        System.out.println(codeUrl);
        return codeUrl;
    }
}
