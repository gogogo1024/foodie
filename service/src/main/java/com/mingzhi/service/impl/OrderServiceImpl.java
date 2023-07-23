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
import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.pojo.bo.SubmitOrderBO;
import com.mingzhi.pojo.vo.ItemSpecVO;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.pojo.vo.OrderVO;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.ItemService;
import com.mingzhi.service.OrderService;
import com.mingzhi.utils.DateUtil;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    public OrderVO creatOrder(SubmitOrderBO submitOrderBO, List<ShopCartBO> shopCartBOList) {
        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSectIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        int postAmount = 0;
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

        int buyCounts = 1;
        order.setPostAmount(postAmount);
        order.setPayMethod(payMethod);
        order.setLeftMsg(leftMsg);
        order.setIsComment(YesOrNo.NO.type);
        order.setIsDelete(YesOrNo.NO.type);
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

        // 4. 构建商户订单
        MerchantOrderVO merchantOrderVO = new MerchantOrderVO();
        merchantOrderVO.setMerchantOrderId(orderId);
        merchantOrderVO.setMerchantUserId(userId);
        merchantOrderVO.setAmount(postAmount + realPayAmount);
        merchantOrderVO.setPayMethod(payMethod);
//        merchantOrderVO.setReturnUrl("");
        // 5. 构建自定义订单
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrderVO(merchantOrderVO);
        return orderVO;
    }

    /**
     * 修改订单状态
     *
     * @param orderId     订单id
     * @param orderStatus 订单状态
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus status = new OrderStatus();
        status.setOrderId(orderId);
        status.setOrderStatus(orderStatus);
        status.setPayTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(status);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrderInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    /**
     * 关闭超时未支付订单
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {
        // 查询超时的未付款订单
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(orderStatus);
        for (OrderStatus os : list) {
            Date createdTime = os.getCreatedTime();
            int diffDay = DateUtil.daysBetween(createdTime, new Date());
            if (diffDay >= 1) {
                doCloseOrder(os.getOrderId());
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void doCloseOrder(String orderId) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);
        orderStatus.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(orderStatus);
    }

    /**
     * 通过商品规格id查询匹配的购物车商品BO
     *
     * @param shopCartBOList 购物车商品 list
     * @param specId         商品规格id
     * @return 匹配的购物车商品BO
     */

    private ShopCartBO getBuyCountsFromList(List<ShopCartBO> shopCartBOList, String specId) {
        for (ShopCartBO shopCartBO : shopCartBOList) {
            if (Objects.equals(shopCartBO.getSpecId(), specId)) {
                return shopCartBO;
            }
        }
        return null;
    }

}
