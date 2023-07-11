package com.mingzhi.service;

import com.mingzhi.pojo.PaymentOrders;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PaymentOrderService {

    /**
     * 创建商户订单
     *
     * @param merchantOrderVO 商户订单VO
     * @return 创建成功与否
     */
    public boolean createPaymentOrder(MerchantOrderVO merchantOrderVO);


    /**
     * @param merchantOrderId 商户id
     * @param merchantUserId  用户id
     * @param paymentStatus   支付订单状态
     * @return 商户支付订单
     */
    public PaymentOrders queryOrderByStatus(String merchantOrderId, String merchantUserId, Integer paymentStatus);

    public void notifyWechatPaid(HttpServletRequest request, HttpServletResponse response);
}
