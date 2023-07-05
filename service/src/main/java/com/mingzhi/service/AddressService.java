package com.mingzhi.service;

import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.UserAddress;

import java.util.List;

public interface AddressService {
    /**
     * 根据用户id查询地址列表
     *
     * @param userId 用户id
     * @return 用户地址列表
     */
    public List<UserAddress> queryAll(String userId);

}
