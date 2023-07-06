package com.mingzhi.service;

import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.Orders;
import com.mingzhi.pojo.bo.SubmitOrderBO;

import java.util.List;

public interface OrderService {
    /**
     * 订单创建
     *
     * @param submitOrderBO 订单创建BO
     */
    public void creatOrder(SubmitOrderBO submitOrderBO);

}
