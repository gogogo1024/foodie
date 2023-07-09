package com.mingzhi.service;

import com.mingzhi.pojo.bo.SubmitOrderBO;

public interface OrderService {
    /**
     * 订单创建
     *
     * @param submitOrderBO 订单创建BO
     */
    public String creatOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     *
     * @param orderId     订单id
     * @param orderStatus 订单状态
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

}
