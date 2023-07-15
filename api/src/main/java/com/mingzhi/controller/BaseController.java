package com.mingzhi.controller;

import com.mingzhi.pojo.Orders;
import com.mingzhi.service.center.MyOrdersService;
import com.mingzhi.utils.MingzhiJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
public class BaseController {
    @Autowired
    private MyOrdersService myOrdersService;

    /**
     * 查询用户自己的订单
     *
     * @param orderId 订单id
     * @param userId  用户id
     * @return 用户订单
     */
    public MingzhiJSONResult checkUserOrder(String orderId, String userId
    ) {

        Orders orders = myOrdersService.queryMyOrder(orderId, userId);
        if (orders == null) {
            return MingzhiJSONResult.errorMsg("订单不存在");
        }

        return MingzhiJSONResult.ok();

    }

}
