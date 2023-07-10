package com.mingzhi.service;

import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.OrderVO;

public interface OrderService {
    /**
     * 订单创建
     *
     * @param submitOrderBO 订单创建BO
     * @return OrderVO OrderVO
     */
    public OrderVO creatOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     *
     * @param orderId     订单id
     * @param orderStatus 订单状态
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

}
