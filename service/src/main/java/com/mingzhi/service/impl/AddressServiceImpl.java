package com.mingzhi.service.impl;

import com.mingzhi.mapper.CarouselMapper;
import com.mingzhi.mapper.UserAddressMapper;
import com.mingzhi.pojo.Carousel;
import com.mingzhi.pojo.UserAddress;
import com.mingzhi.service.AddressService;
import com.mingzhi.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;

    /**
     * 根据用户id查询地址列表
     *
     * @param userId 用户id
     * @return 用户地址列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress ua = new UserAddress();
        ua.setUserId(userId);
        return userAddressMapper.select(ua);
    }
}
