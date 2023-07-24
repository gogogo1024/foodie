package com.mingzhi.pojo.vo;

import com.mingzhi.pojo.bo.ShopCartBO;

import java.util.List;

/**
 * 订单VO
 */
public class OrderVO {
    private String orderId;
    private MerchantOrderVO merchantOrderVO;


    private List<ShopCartBO> shopCartBOList;

    public List<ShopCartBO> getShopCartBOList() {
        return shopCartBOList;
    }

    public void setShopCartBOList(List<ShopCartBO> shopCartBOList) {
        this.shopCartBOList = shopCartBOList;
    }

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
