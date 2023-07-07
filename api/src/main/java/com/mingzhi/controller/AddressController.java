package com.mingzhi.controller;

import com.mingzhi.pojo.UserAddress;
import com.mingzhi.pojo.bo.AddressBO;
import com.mingzhi.service.AddressService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Tag(value = "地址接口", tags = {"地址接口"})
@Tag(name = "操作接口", description = "操作描述")
@RestController()
@ResponseBody()
@RequestMapping("address")
public class AddressController {
    final static Logger logger = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    private AddressService addressService;

    @Operation(summary = "查询用户地址列表", description = "查询用户地址列表", method = "POST")
    @PostMapping("/list")
    public MingzhiJSONResult queryAll(
            @Parameter(name = "userId", required = true)
            @RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        List<UserAddress> list = addressService.queryAll(userId);
        return MingzhiJSONResult.ok(list);
    }

    @Operation(summary = "用户添加新地址", description = "用户添加新地址", method = "POST")
    @PostMapping("/add")
    public MingzhiJSONResult addUserNewAddress(
            @Parameter(name = "addressBO", required = true)
            @RequestBody AddressBO addressBO) {
        // TODO 参数校验addressBO
        addressService.addUserNewAddress(addressBO);
        return MingzhiJSONResult.ok();
    }

    @Operation(summary = "用户修改地址", description = "用户修改地址", method = "PUT")
    @PutMapping("/update")
    public MingzhiJSONResult updateUserAddress(
            @Parameter(name = "addressBO", required = true)
            @RequestBody AddressBO addressBO) {
        addressService.updateUserNewAddress(addressBO);
        return MingzhiJSONResult.ok();
    }

    @Operation(summary = "用户删除地址", description = "用户删除地址", method = "DELETE")
    @DeleteMapping("/delete")
    public MingzhiJSONResult delUserAddress(
            @Parameter(name = "userId", required = true)
            @RequestParam String userId,
            @Parameter(name = "addressId", required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return MingzhiJSONResult.errorMsg("地址id或者用户id不能为空");

        }
        addressService.delUserAddress(userId, addressId);
        return MingzhiJSONResult.ok();
    }

    @Operation(summary = "用户设置默认地址", description = "用户删除地址", method = "PATCH")
    @PatchMapping("/setDefault")
    public MingzhiJSONResult setDefaultUserAddress(
            @Parameter(name = "userId", required = true)
            @RequestParam String userId,
            @Parameter(name = "addressId", required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)) {
            return MingzhiJSONResult.errorMsg("地址id或者用户id不能为空");

        }
        addressService.setDefaultUserAddress(userId, addressId);
        return MingzhiJSONResult.ok();
    }
}
