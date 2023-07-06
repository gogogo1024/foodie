package com.mingzhi.service.impl;

import com.mingzhi.enums.Sex;
import com.mingzhi.mapper.UsersMapper;
import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.bo.UserBO;
import com.mingzhi.service.UserService;
import com.mingzhi.utils.DateUtil;
import com.mingzhi.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    private static final String USER_FACE = "https://image.16pic.com/00/10/39/16pic_1039293_s.jpg?imageslim";
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 布尔值
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Boolean queryUsernameExits(String username) {
        Example useExample = new Example(Users.class);
        Example.Criteria userCriteria = useExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        Users user = usersMapper.selectOneByExample(useExample);
        return user != null;
    }

    /**
     * 创建用户
     *
     * @param userBO userBO
     * @return 用户
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {
        Users user = new Users();
        user.setUsername(userBO.getUsername());
        try {
            user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        user.setNickname(userBO.getUsername());
        user.setFace(USER_FACE);
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());
        user.setId(sid.nextShort());
        usersMapper.insert(user);
        return user;
    }

    /**
     * 查询用户名和密码是否匹配，用于登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example useExample = new Example(Users.class);
        Example.Criteria userCriteria = useExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);
        return usersMapper.selectOneByExample(useExample);
    }
}
