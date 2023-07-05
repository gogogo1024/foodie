package com.mingzhi.controller;

import com.mingzhi.pojo.UserAddress;
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


}
