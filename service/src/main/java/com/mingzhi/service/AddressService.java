package com.mingzhi.service;

import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.UserAddress;
import com.mingzhi.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    /**
     * 根据用户id查询地址列表
     *
     * @param userId 用户id
     * @return 用户地址列表
     */
    public List<UserAddress> queryAll(String userId);

    /**
     * 用户添加新地址
     *
     * @param addressBO 用户地址BO
     */
    public void addUserNewAddress(AddressBO addressBO);

    /**
     * 用户修改地址
     *
     * @param addressBO 用户地址BO
     */
    public void updateUserNewAddress(AddressBO addressBO);

    /**
     * 用户删除地址
     *
     * @param userId    用户id
     * @param addressId 用户地址id
     */
    public void delUserAddress(String userId, String addressId);

    /**
     * 用户设置默认地址
     *
     * @param userId    用户id
     * @param addressId 用户地址id
     */
    public void setDefaultUserAddress(String userId, String addressId);

    /**
     * 根据用户id和地址id，查询地址信息
     *
     * @param userId    用户id
     * @param addressId 用户地址id
     * @return 地址信息
     */
    public UserAddress queryUserAddress(String userId, String addressId);


}
