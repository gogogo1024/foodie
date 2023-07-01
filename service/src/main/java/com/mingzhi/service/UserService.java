package com.mingzhi.service;

import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.bo.UserBO;

public interface UserService {
    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return 布尔值
     */
    public Boolean queryUsernameExits(String username);

    /**
     * 创建用户
     *
     * @param userBO userBO
     * @return 用户
     */
    public Users createUser(UserBO userBO);

    /**
     * 查询用户名和密码是否匹配，用于登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */

    public Users queryUserForLogin(String username, String password);
}
