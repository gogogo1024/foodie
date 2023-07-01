package com.mingzhi.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "用户对象模型", description = "用户传入数据实体模型")
public class UserBO {
    @ApiModelProperty(value = "用户名", name = "username", required = true)
    private String username;
    @ApiModelProperty(value = "用户密码", name = "password", required = true)
    private String password;
    @ApiModelProperty(value = "用户确认密码", name = "confirmPassword", required = false)
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
