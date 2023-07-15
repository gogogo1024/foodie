package com.mingzhi.service.impl.center;

import com.github.pagehelper.PageHelper;
import com.mingzhi.enums.YesOrNo;
import com.mingzhi.mapper.*;
import com.mingzhi.pojo.ItemsComments;
import com.mingzhi.pojo.OrderItems;
import com.mingzhi.pojo.OrderStatus;
import com.mingzhi.pojo.Orders;
import com.mingzhi.pojo.vo.MyCommentVO;
import com.mingzhi.service.BaseService;
import com.mingzhi.service.center.MyCommentsService;
import com.mingzhi.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MyCommentsServiceImpl extends BaseService implements MyCommentsService {
    @Autowired
    private OrdersMapperCustom ordersMapperCustom;
    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private ItemsCommentsMapper itemsCommentsMapper;
    @Autowired
    private ItemsCommentsMapperCustom itemsCommentsMapperCustom;


    /**
     * 根据订单id查询关联商品
     *
     * @param orderId 订单id
     * @return 订单商品列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)

    @Override
    public List<OrderItems> queryPendingComment(String orderId) {
        OrderItems query = new OrderItems();
        query.setOrderId(orderId);
        return orderItemsMapper.select(query);
    }

    /**
     * 保存用户商品评价
     *
     * @param orderId 订单id
     * @param userId  商品id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(String orderId, String userId, List<ItemsComments> commentList) {
        Date current = new Date();
        for (ItemsComments bo : commentList) {
            bo.setId(sid.nextShort());
            bo.setUserId(userId);
            bo.setCreatedTime(current);
            bo.setUpdatedTime(current);
        }
        itemsCommentsMapper.insertList(commentList);

        Orders orders = new Orders();
        orders.setIsComment(YesOrNo.YES.type);
        orders.setId(orderId);
        ordersMapper.updateByPrimaryKeySelective(orders);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCommentTime(current);
        orderStatus.setOrderId(orderId);
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    /**
     * 查询用户对商品对评论列表
     *
     * @param userId   订单id
     * @param page     第几页
     * @param pageSize 每页返回多少条
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        PageHelper.startPage(page, pageSize);
        List<MyCommentVO> list = itemsCommentsMapperCustom.queryMyComments(map);
        return setterPagedGrid(list, page);
    }
}
