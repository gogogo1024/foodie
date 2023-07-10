package com.mingzhi.pojo.vo;

/**
 * 订单VO
 */
public class OrderVO {
    private String orderId;
    private MerchantOrderVO merchantOrderVO;

    public MerchantOrderVO getMerchantOrderVO() {
        return merchantOrderVO;
    }

    public void setMerchantOrderVO(MerchantOrderVO merchantOrderVO) {
        this.merchantOrderVO = merchantOrderVO;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


}
