package com.mingzhi.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.mingzhi.enums.OrderStatusEnum;
import com.mingzhi.enums.YesOrNo;
import com.mingzhi.mapper.OrderStatusMapper;
import com.mingzhi.mapper.OrdersMapper;
import com.mingzhi.mapper.OrdersMapperCustom;
import com.mingzhi.pojo.OrderStatus;
import com.mingzhi.pojo.Orders;
import com.mingzhi.pojo.vo.MyOrdersVO;
import com.mingzhi.pojo.vo.OrderStatusCountsVO;
import com.mingzhi.service.center.MyOrdersService;
import com.mingzhi.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyOrdersServiceImpl implements MyOrdersService {
    @Autowired
    private OrdersMapperCustom ordersMapperCustom;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * @param userId      用户id
     * @param orderStatus 订单状态
     * @param page        第几页
     * @param pageSize    每页多少条
     * @return 已分页的个人订单
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        if (orderStatus != null) {
            map.put("orderStatus", orderStatus);
        }
        PageHelper.startPage(page, pageSize);
        List<MyOrdersVO> list = ordersMapperCustom.queryMyOrders(map);
        return PagedGridResult.setterPagedGrid(list, page);
    }

    /**
     * 发货
     *
     * @param orderId 订单id
     */
    @Transactional(propagation = Propagation.REQUIRED)

    @Override
    public void updateDeliverOrderStatus(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
        orderStatus.setDeliverTime(new Date());
        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        orderStatusMapper.updateByExampleSelective(orderStatus, example);
    }

    /**
     * 确认收货
     *
     * @param orderId 订单id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean updateReceiveOrderStatus(String orderId) {
        OrderStatus updateOrder = new OrderStatus();
        updateOrder.setOrderStatus(OrderStatusEnum.SUCCESS.type);
        updateOrder.setSuccessTime(new Date());

        Example example = new Example(OrderStatus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId", orderId);
        criteria.andEqualTo("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);

        int result = orderStatusMapper.updateByExampleSelective(updateOrder, example);

        return result == 1;

    }

    /**
     * 删除订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean deleteOrder(String userId, String orderId) {
        Orders order = new Orders();
        order.setIsDelete(YesOrNo.YES.type);
        order.setUpdatedTime(new Date());

        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        criteria.andEqualTo("userId", userId);

        int result = ordersMapper.updateByExampleSelective(order, example);
        return result == 1;
    }

    /**
     * 查询用户订单
     *
     * @param userId  用户id
     * @param orderId 订单id
     * @return 用户订单
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Orders queryMyOrder(String userId, String orderId) {
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setId(orderId);
        orders.setIsDelete(YesOrNo.NO.type);

        return ordersMapper.selectOne(orders);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult getOrdersTrend(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        PageHelper.startPage(page, pageSize);
        List<OrderStatus> list = ordersMapperCustom.getMyOrderTrend(map);
        return PagedGridResult.setterPagedGrid(list, page);
    }

    /**
     * 查询用户订单数
     *
     * @param userId 用户id
     * @return 用户订单数
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatusCountsVO getMyOrderStatusCounts(String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("orderStatus", OrderStatusEnum.WAIT_PAY.type);
        int waitPayCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_DELIVER.type);
        int waitDeliverCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.WAIT_RECEIVE.type);
        int waitReceiveCounts = ordersMapperCustom.getMyOrderStatusCounts(map);

        map.put("orderStatus", OrderStatusEnum.SUCCESS.type);
        map.put("isComment", YesOrNo.NO.type);
        int waitSuccessCounts = ordersMapperCustom.getMyOrderStatusCounts(map);
        OrderStatusCountsVO orderStatusCountsVO = new OrderStatusCountsVO(
                waitPayCounts,
                waitDeliverCounts,
                waitReceiveCounts,
                waitSuccessCounts
        );


        return orderStatusCountsVO;
    }
}
