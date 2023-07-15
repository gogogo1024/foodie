package com.mingzhi.service.center;

import com.mingzhi.pojo.ItemsComments;
import com.mingzhi.pojo.OrderItems;
import com.mingzhi.utils.PagedGridResult;

import java.util.List;

public interface MyCommentsService {


    /**
     * 根据订单id查询关联商品
     *
     * @param orderId 订单id
     * @return 订单商品列表
     */
    public List<OrderItems> queryPendingComment(String orderId);

    /**
     * 保存用户商品评价
     *
     * @param orderId     订单id
     * @param userId      商品id
     * @param commentList 商品评论
     */
    public void saveComment(String orderId, String userId, List<ItemsComments> commentList);

    /**
     * 查询用户对商品对评论列表
     *
     * @param userId   订单id
     * @param page     第几页
     * @param pageSize 每页返回多少条
     */
    public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize);

}
