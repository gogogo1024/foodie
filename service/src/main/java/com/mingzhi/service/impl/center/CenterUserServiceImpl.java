package com.mingzhi.service.impl.center;

import com.mingzhi.mapper.UsersMapper;
import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.bo.center.CenterUserBO;
import com.mingzhi.service.center.CenterUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CenterUserServiceImpl implements CenterUserService {
    @Autowired
    public UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users user = usersMapper.selectByPrimaryKey(userId);
        user.setPassword(null);
        return user;
    }

    /**
     * 修改用户信息
     *
     * @param centerUserBO 用户中心BO
     * @param userId       用户id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
        Users user = new Users();
        BeanUtils.copyProperties(centerUserBO, user);
        user.setId(userId);
        user.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserInfo(userId);

    }

    /**
     * 修改用户头像
     *
     * @param userId  用户id
     * @param faceUrl 用户头像地址
     * @return 用户信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String userId, String faceUrl) {
        Users user = new Users();
        user.setId(userId);
        user.setFace(faceUrl);
        user.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(user);
        return queryUserInfo(userId);
    }

}
