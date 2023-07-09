package com.mingzhi.service;

import com.mingzhi.pojo.bo.WeChatPayBO;

/**
 * 微信支付Service
 */
public interface WechatPayService {
    /**
     * 获取微信Native支付二维码url
     *
     * @param weChatPayBO 微信支付BO
     * @return 二维码url
     */
    public String getCodeUrl(WeChatPayBO weChatPayBO);
}
