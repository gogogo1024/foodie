package com.mingzhi.service.center;

import com.mingzhi.pojo.Orders;
import com.mingzhi.pojo.vo.OrderStatusCountsVO;
import com.mingzhi.utils.PagedGridResult;

public interface MyOrdersService {
    /**
     * @param userId      用户id
     * @param orderStatus 订单状态
     * @param page        第几页
     * @param pageSize    每页多少条
     * @return 已分页的个人订单
     */
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);

    /**
     * 发货
     *
     * @param orderId 订单id
     */
    public void updateDeliverOrderStatus(String orderId);

    /**
     * 确认收货
     *
     * @param orderId 订单id
     */
    public boolean updateReceiveOrderStatus(String orderId);

    /**
     * 删除订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     */
    public boolean deleteOrder(String userId, String orderId);

    /**
     * 查询用户订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @return 用户订单
     */
    public Orders queryMyOrder(String userId, String orderId);

    PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize);

    /**
     * 查询用户订单数
     *
     * @param userId 用户id
     * @return 用户订单数
     */
    public OrderStatusCountsVO getMyOrderStatusCounts(String userId);
}
