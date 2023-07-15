package com.mingzhi.service.center;

import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.bo.center.CenterUserBO;

public interface CenterUserService {
    /**
     * 根据用户id查询用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    public Users queryUserInfo(String userId);

    /**
     * 修改用户信息
     *
     * @param centerUserBO 用户中心BO
     * @param userId       用户id
     * @return 用户信息
     */
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

    /**
     * 修改用户头像
     *
     * @param userId  用户id
     * @param faceUrl 用户头像地址
     * @return 用户信息
     */
    public Users updateUserFace(String userId, String faceUrl);
}
