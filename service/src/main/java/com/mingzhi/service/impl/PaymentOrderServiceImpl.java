package com.mingzhi.service.impl;

import com.mingzhi.enums.PayStatusEnum;
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
import tk.mybatis.mapper.entity.Example;

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
        paymentOrder.setIsDelete(YesOrNo.NO.type);
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

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String updatePaymentOrderPaid(
            String merchantOrderId, Integer paidAmount) {

//        // 构造 RequestParam
//        com.wechat.pay.java.core.notification.RequestParam requestParam = new com.wechat.pay.java.core.notification.RequestParam.Builder()
//                .serialNumber("wechatPayCertificateSerialNumber")
//                .nonce("nonce")
//                .signature("signature")
//                .timestamp("timestamp")
//                .body("requestBody")
//                .build();
//
//// 如果已经初始化了 RSAAutoCertificateConfig，可直接使用
//// 没有的话，则构造一个
//        NotificationConfig config = new RSAAutoCertificateConfig.Builder()
//                .merchantId("merchantId")
//                .privateKeyFromPath("privateKeyPath")
//                .merchantSerialNumber("merchantSerialNumber")
//                .apiV3Key("apiV3key")
//                .build();
//
//// 初始化 NotificationParser
//        NotificationParser parser = new NotificationParser(config);
//
//// 以支付通知回调为例，验签、解密并转换成 Transaction
//        Transaction transaction = parser.parse(requestParam, Transaction.class);

        Example example = new Example(PaymentOrders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("merchantOrderId", "orderId");

        PaymentOrders paymentOrders = new PaymentOrders();
        paymentOrders.setPayStatus(PayStatusEnum.PAID.type);
        paymentOrders.setAmount(1);
        // 选择性更新
        paymentOrderMapper.updateByExampleSelective(paymentOrders, example);
        return queryMerchantReturnUrl(merchantOrderId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public String queryMerchantReturnUrl(String merchantOrderId) {

        PaymentOrders paymentOrders = new PaymentOrders();
        paymentOrders.setMerchantOrderId(merchantOrderId);
        PaymentOrders order = paymentOrderMapper.selectOne(paymentOrders);

        return paymentOrders.getReturnUrl();
    }
}
