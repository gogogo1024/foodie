package com.mingzhi.service.impl;

import com.mingzhi.enums.OrderStatusEnum;
import com.mingzhi.enums.YesOrNo;
import com.mingzhi.mapper.OrderItemsMapper;
import com.mingzhi.mapper.OrderStatusMapper;
import com.mingzhi.mapper.OrdersMapper;
import com.mingzhi.pojo.OrderItems;
import com.mingzhi.pojo.OrderStatus;
import com.mingzhi.pojo.Orders;
import com.mingzhi.pojo.UserAddress;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.ItemSpecVO;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.ItemService;
import com.mingzhi.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderStatusMapper orderStatusMapper;

    /**
     * 订单创建
     *
     * @param submitOrderBO 订单创建BO
     */

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void creatOrder(SubmitOrderBO submitOrderBO) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSectIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        Integer postAmount = 0;
        Orders order = new Orders();
        String orderId = sid.nextShort();
        order.setId(orderId);
        order.setUserId(userId);
        UserAddress userAddress = addressService.queryUserAddress(userId, addressId);
        order.setReceiverAddress(userAddress.getProvince() + " "
                + userAddress.getCity() + " "
                + userAddress.getDistrict() + " "
                + userAddress.getDetail() + " ");
        order.setReceiverName(userAddress.getReceiver());
        order.setReceiverMobile(userAddress.getMobile());

        // TODO 商品购买数量重新从redis中获取
        int buyCounts = 1;
        order.setPostAmount(postAmount);
        order.setPayMethod(payMethod);
        order.setLeftMsg(leftMsg);
        order.setIsComment(YesOrNo.No.type);
        order.setIsDelete(YesOrNo.No.type);
        Date currentDate = new Date();
        order.setCreatedTime(currentDate);
        order.setUpdatedTime(currentDate);
        String[] itemSpecIdArr = itemSectIds.split(",");
        int totalAmount = 0;
        int realPayAmount = 0;
        List<ItemSpecVO> itemSpecVOList = itemService.queryItemsSpecBySpecIds(itemSectIds);
        List<OrderItems> list = new ArrayList<>();

        for (ItemSpecVO itemSpecVO : itemSpecVOList) {
            totalAmount += itemSpecVO.getPriceNormal() * buyCounts;
            realPayAmount += itemSpecVO.getPriceDiscount() * buyCounts;
            OrderItems orderItems = new OrderItems();
//            orderItems.setItemId(itemSpecVO.getItemId());
//            orderItems.setItemName(itemSpecVO.getItemName());
//            orderItems.setItemImg(itemSpecVO.getItemImgUrl());
//            orderItems.setBuyCounts(buyCounts);
//            orderItems.setItemSpecId(itemSpecVO.getSpecId());
//            orderItems.setItemSpecName(itemSpecVO.getSpecName());

            String[] ignoreProperties = new String[]{
                    "priceNormal",
                    "priceDiscount",
            };
            BeanUtils.copyProperties(itemSpecVO, orderItems, ignoreProperties);
            orderItems.setId(sid.nextShort());
            orderItems.setOrderId(orderId);
            orderItems.setPrice(itemSpecVO.getPriceDiscount());
            orderItems.setBuyCounts(buyCounts);
            list.add(orderItems);
            //  减库存
            itemService.decreaseItemSpecStock(itemSpecVO.getItemSpecId(), buyCounts);
        }
        // 1. 订单详情
        orderItemsMapper.insertList(list);

        order.setTotalAmount(totalAmount);
        order.setRealPayAmount(realPayAmount);
        // 2. 订单保存
        ordersMapper.insert(order);


        // 3. 订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        currentDate = new Date();
        orderStatus.setCreatedTime(currentDate);
        orderStatusMapper.insert(orderStatus);
    }

}
