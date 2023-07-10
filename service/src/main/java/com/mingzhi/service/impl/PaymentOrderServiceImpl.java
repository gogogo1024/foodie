package com.mingzhi.service.impl;

import com.mingzhi.enums.OrderStatusEnum;
import com.mingzhi.enums.YesOrNo;
import com.mingzhi.mapper.PaymentOrderMapper;
import com.mingzhi.pojo.PaymentOrders;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.service.PaymentOrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService {
    @Autowired
    private Sid sid;
    @Autowired
    private PaymentOrderMapper paymentOrderMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean createPaymentOrder(MerchantOrderVO merchantOrderVO) {
        String id = sid.nextShort();

        PaymentOrders paymentOrder = new PaymentOrders();
        BeanUtils.copyProperties(merchantOrderVO, paymentOrder);

        paymentOrder.setId(id);
        paymentOrder.setPayStatus(OrderStatusEnum.WAIT_PAY.type);
        paymentOrder.setIsDelete(YesOrNo.No.type);
        paymentOrder.setCreatedTime(new Date());

        int result = paymentOrderMapper.insert(paymentOrder);
        return result == 1;
    }

    /**
     * @param merchantOrderId 商户id
     * @param merchantUserId  用户id
     * @param orderStatus     订单状态
     * @return 商户支付订单
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PaymentOrders queryOrderByStatus(String merchantOrderId, String merchantUserId, Integer orderStatus) {
        PaymentOrders queryOrder = new PaymentOrders();
        queryOrder.setMerchantOrderId(merchantOrderId);
        queryOrder.setMerchantUserId(merchantUserId);
        queryOrder.setPayStatus(orderStatus);
        return paymentOrderMapper.selectOne(queryOrder);
    }
}
