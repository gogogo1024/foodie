package com.mingzhi.controller.center;

import com.mingzhi.pojo.Users;
import com.mingzhi.service.center.CenterUserService;
import com.mingzhi.utils.MingzhiJSONResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户中心", description = "用户中心")
@RestController()
@ResponseBody()
@RequestMapping("center")
public class CenterController {
    @Autowired
    private CenterUserService centerUserService;

    @Operation(summary = "获取用户信息", description = "获取用户信息", method = "GET")
    @GetMapping("userInfo")
    public MingzhiJSONResult userInfo(
            @Parameter(name = "userId", required = true, description = "用户id")
            @RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return MingzhiJSONResult.errorMsg(null);
        }
        Users user = centerUserService.queryUserInfo(userId);
        return MingzhiJSONResult.ok(user);

    }
}
