package com.mingzhi.service.impl;

import com.mingzhi.enums.YesOrNo;
import com.mingzhi.mapper.UserAddressMapper;
import com.mingzhi.pojo.UserAddress;
import com.mingzhi.pojo.bo.AddressBO;
import com.mingzhi.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;

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

    /**
     * 用户添加新地址
     *
     * @param addressBO 用户地址BO
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addUserNewAddress(AddressBO addressBO) {
        int isDefault = 0;
        List<UserAddress> list = this.queryAll(addressBO.getUserId());
        if (list == null || list.isEmpty()) {
            isDefault = 1;
        }
        String addressId = sid.nextShort();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setId(addressId);
        userAddress.setIsDefault(isDefault);
        Date currentDate = new Date();
        userAddress.setCreatedTime(currentDate);
        userAddress.setUpdatedTime(currentDate);
        userAddressMapper.insert(userAddress);
    }

    /**
     * 用户修改地址
     *
     * @param addressBO 用户地址BO
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserNewAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setId(addressId);
        Date currentDate = new Date();
        userAddress.setUpdatedTime(currentDate);
        // 选择性更新
        userAddressMapper.updateByPrimaryKeySelective(userAddress);

    }

    /**
     * 用户删除地址
     *
     * @param userId    用户id
     * @param addressId 用户地址id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void delUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        userAddress.setUserId(userId);
        userAddressMapper.delete(userAddress);
    }

    /**
     * 用户设置默认地址
     *
     * @param userId    用户id
     * @param addressId 用户地址id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void setDefaultUserAddress(String userId, String addressId) {
        UserAddress queryUserAddress = new UserAddress();
        queryUserAddress.setId(addressId);
        queryUserAddress.setUserId(userId);
        queryUserAddress.setIsDefault(YesOrNo.Yes.type);
        UserAddress ua = userAddressMapper.selectOne(queryUserAddress);
        if (ua == null) {
            // 1. 查询之前默认地址修改为非默认
            UserAddress queryUserAddress2 = new UserAddress();
            queryUserAddress2.setUserId(userId);
            queryUserAddress2.setIsDefault(YesOrNo.Yes.type);
            UserAddress preDefaultUser = userAddressMapper.selectOne(queryUserAddress2);
            if (preDefaultUser != null) {
                preDefaultUser.setIsDefault(YesOrNo.No.type);
                userAddressMapper.updateByPrimaryKeySelective(preDefaultUser);
            }

            // 2.更新addressId为默认
            userAddressMapper.updateByPrimaryKeySelective(queryUserAddress);

        }

    }

    /**
     * 根据用户id和地址id，查询地址信息
     *
     * @param userId    用户id
     * @param addressId 用户地址id
     * @return 地址信息
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress queryUserAddress = new UserAddress();
        queryUserAddress.setId(addressId);
        queryUserAddress.setUserId(userId);
        return userAddressMapper.selectOne(queryUserAddress);
    }
}
