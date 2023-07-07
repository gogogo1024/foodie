package com.mingzhi.pojo.bo;

//@Schema(name = "用户对象模型", description = "用户传入数据实体模型")
public class UserBO {
    //    @Schema(name = "username")
    private String username;
    //    @Schema(name = "用户密码")
    private String password;
    //    @Schema(name = "用户确认密码")
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
