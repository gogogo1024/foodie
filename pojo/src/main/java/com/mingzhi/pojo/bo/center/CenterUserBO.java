package com.mingzhi.pojo.bo.center;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Schema(name = "用户对象", description = "从客户端，由用户传入的数据封装在此entity中")
public class CenterUserBO {

    @Schema(name = "用户名", example = "json")
    private String username;
    @Schema(name = "密码", example = "123456")
    private String password;
    @Schema(name = "确认密码", example = "123456")
    private String confirmPassword;


    @Length(max = 12, message = "用户真实姓名不能超过12位")
    @NotBlank(message = "用户昵称不能为空")
    @Schema(name = "用户昵称", example = "杰森")
    private String nickname;

    @Length(max = 12, message = "用户真实姓名不能超过12位")
    @Schema(name = "真实姓名", example = "黄新一")
    private String realname;

    @Pattern(regexp = "^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}|(19[0-9]{1})))+\\d{8})$", message = "手机号格式不正确")
    @Schema(name = "手机号", example = "13999999999")
    private String mobile;

    @Email
    @Schema(name = "邮箱地址", example = "abc@mmail.com")
    private String email;

    @Min(value = 0, message = "性别选择不正确")
    @Max(value = 2, message = "性别选择不正确")
    @Schema(name = "性别", example = "0:女 1:男 2:保密")
    private Integer sex;
    @Schema(name = "生日", example = "1900-01-01")
    private Date birthday;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "CenterUserBO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", nickname='" + nickname + '\'' +
                ", realname='" + realname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", sex=" + sex +
                ", birthday=" + birthday +
                '}';
    }

}