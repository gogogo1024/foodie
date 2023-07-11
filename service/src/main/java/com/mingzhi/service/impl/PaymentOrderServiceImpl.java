package com.mingzhi.service.impl;

import com.mingzhi.enums.PayStatusEnum;
import com.mingzhi.enums.YesOrNo;
import com.mingzhi.mapper.PaymentOrderMapper;
import com.mingzhi.pojo.PaymentOrders;
import com.mingzhi.pojo.vo.MerchantOrderVO;
import com.mingzhi.service.PaymentOrderService;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

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
        paymentOrder.setPayStatus(PayStatusEnum.UNPAID.type);
        paymentOrder.setIsDelete(YesOrNo.No.type);
        paymentOrder.setCreatedTime(new Date());

        int result = paymentOrderMapper.insert(paymentOrder);
        return result == 1;
    }

    /**
     * @param merchantOrderId 商户id
     * @param merchantUserId  用户id
     * @param paymentStatus   支付订单状态
     * @return 商户支付订单
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PaymentOrders queryOrderByStatus(String merchantOrderId, String merchantUserId, Integer paymentStatus) {
        PaymentOrders queryOrder = new PaymentOrders();
        queryOrder.setMerchantOrderId(merchantOrderId);
        queryOrder.setMerchantUserId(merchantUserId);
        queryOrder.setPayStatus(paymentStatus);
        return paymentOrderMapper.selectOne(queryOrder);
    }

    @Operation(summary = "微信支付回调通知", description = "微信支付回调通知", method = "POST")
    @PostMapping("/notifyWechatPaid")
    public void notifyWechatPaid(
            HttpServletRequest request, HttpServletResponse response) {

        // 构造 RequestParam
        com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
                .serialNumber("wechatPayCertificateSerialNumber")
                .nonce("nonce")
                .signature("signature")
                .timestamp("timestamp")
                .body("requestBody")
                .build();

// 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
// 没有的话，则构造一个
        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
                .merchantId("merchantId")
                .privateKeyFromPath("privateKeyPath")
                .merchantSerialNumber("merchantSerialNumber")
                .apiV3Key("apiV3key")
                .build();

// 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(config);

// 以支付通知回调为例，验签、解密并转换成 Transaction
        Transaction transaction = parser.parse(requestParam, Transaction.class);

        PaymentOrders paymentOrders = new PaymentOrders();
        paymentOrders.setId("orderId");
        Date currentDate = new Date();
        // 选择性更新
        paymentOrderMapper.updateByPrimaryKeySelective(paymentOrders);

//        orderService.updateOrderStatus(orderId, OrderStatusEnum.WAIT_DELIVER.type);
//        return HttpStatus.OK.value();
    }
}
