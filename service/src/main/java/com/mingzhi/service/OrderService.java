package com.mingzhi.service;

import com.mingzhi.pojo.OrderStatus;
import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    /**
     * 订单创建
     *
     * @param submitOrderBO 订单创建BO
     * @return OrderVO OrderVO
     */
    public OrderVO creatOrder(SubmitOrderBO submitOrderBO, List<ShopCartBO> list);

    /**
     * 修改订单状态
     *
     * @param orderId     订单id
     * @param orderStatus 订单状态
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     *
     * @param orderId 订单id
     * @return 订单状态数据
     */
    public OrderStatus queryOrderInfo(String orderId);

    /**
     * 关闭超时未支付订单
     */
    public void closeOrder();

}
