package com.mingzhi.controller;

import com.mingzhi.pojo.UserAddress;
import com.mingzhi.pojo.bo.AddressBO;
import com.mingzhi.service.AddressService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(value = "地址接口", tags = {"地址接口"})
@RestController()
@RequestMapping("address")
public class AddressController {
    final static Logger logger = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "查询用户地址列表", notes = "查询用户地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public MingzhiJSONResult queryAll(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        List<UserAddress> list = addressService.queryAll(userId);
        return MingzhiJSONResult.ok(list);
    }

    @ApiOperation(value = "用户添加新地址", notes = "用户添加新地址", httpMethod = "POST")
    @PostMapping("/add")
    public MingzhiJSONResult addUserNewAddress(
            @ApiParam(name = "addressBO", value = "addressBO", required = true)
            @RequestBody AddressBO addressBO) {
        // TODO 参数校验addressBO
        addressService.addUserNewAddress(addressBO);
        return MingzhiJSONResult.ok();
    }

    @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "PUT")
    @PutMapping("/update")
    public MingzhiJSONResult updateUserAddress(
            @ApiParam(name = "addressBO", value = "addressBO", required = true)
            @RequestBody AddressBO addressBO) {
        addressService.updateUserNewAddress(addressBO);
        return MingzhiJSONResult.ok();
    }

    @ApiOperation(value = "用户删除地址", notes = "用户删除地址", httpMethod = "DELETE")
    @DeleteMapping("/delete")
    public MingzhiJSONResult delUserAddress(
            @ApiParam(name = "userId", value = "userId", required = true)
            @RequestParam String userId,
            @ApiParam(name = "addressId", value = "addressId", required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return MingzhiJSONResult.errorMsg("地址id或者用户id不能为空");

        }
        addressService.delUserAddress(userId, addressId);
        return MingzhiJSONResult.ok();
    }

    @ApiOperation(value = "用户设置默认地址", notes = "用户删除地址", httpMethod = "PATCH")
    @PatchMapping("/setDefault")
    public MingzhiJSONResult setDefaultUserAddress(
            @ApiParam(name = "userId", value = "userId", required = true)
            @RequestParam String userId,
            @ApiParam(name = "addressId", value = "addressId", required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return MingzhiJSONResult.errorMsg("地址id或者用户id不能为空");

        }
        addressService.setDefaultUserAddress(userId, addressId);
        return MingzhiJSONResult.ok();
    }
}
