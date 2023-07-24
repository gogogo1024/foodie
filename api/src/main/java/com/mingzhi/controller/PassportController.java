package com.mingzhi.controller;

import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.bo.ShopCartBO;
import com.mingzhi.pojo.bo.UserBO;
import com.mingzhi.service.UserService;
import com.mingzhi.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "注册登录", description = "用户注册登录")
@RestController()
@ResponseBody()
@RequestMapping("passport")
public class PassportController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

    @Operation(summary = "用户名是否存在", description = "用户名是否存在", method = "GET")
    @GetMapping("/usernameIsExist")

    @ResponseBody
    public MingzhiJSONResult usernameIsExist(@RequestParam String username) {
        if (StringUtils.isBlank(username)) {
            return MingzhiJSONResult.errorMsg("用户名不能为空");
        }
        boolean isExist = userService.queryUsernameExits(username);
        if (isExist) {
            return MingzhiJSONResult.errorMsg("用户名已经存在");
        }
        return MingzhiJSONResult.ok();

    }

    @Operation(summary = "用户注册", description = "用户注册", method = "POST")
    @PostMapping("/register")
    public MingzhiJSONResult register(@RequestBody UserBO userBO,
                                      HttpServletRequest request,
                                      HttpServletResponse response
    ) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(confirmPassword)
        ) {
            return MingzhiJSONResult.errorMsg("用户名或者密码不能空");

        }
        boolean isExist = userService.queryUsernameExits(username);
        if (isExist) {
            return MingzhiJSONResult.errorMsg("用户名已经存在");
        }
        if (password.length() < 6) {
            return MingzhiJSONResult.errorMsg("密码长度不能小于6");
        }
        if (!password.equals(confirmPassword)) {
            return MingzhiJSONResult.errorMsg("两次密码不一致");
        }

        Users user = userService.createUser(userBO);
        PojoUtils.setPojoNullProperty(user, new String[]{
                "password",
                "unknownProperty",
                "createdTime",
        });

        syncShopCartToCookieRedis(user.getId(), request, response);
        return MingzhiJSONResult.ok();
    }

    @Operation(summary = "用户登录", description = "用户登录", method = "POST")
    @PostMapping("/login")
    public MingzhiJSONResult login(@RequestBody UserBO userBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws NoSuchAlgorithmException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
        ) {
            return MingzhiJSONResult.errorMsg("用户名或者密码不能空");

        }
        Users user = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (user == null) {
            return MingzhiJSONResult.errorMsg("用户名或者密码不正确");

        }

        PojoUtils.setPojoNullProperty(user, new String[]{
                "password",
                "unknownProperty",
                "createdTime",
        });
        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(user), true);

        syncShopCartToCookieRedis(user.getId(), request, response);
        return MingzhiJSONResult.ok(user);

    }

    @Operation(summary = "用户退出登录", description = "用户退出登录", method = "POST")
    @PostMapping("/logout")
    public MingzhiJSONResult logout(@RequestParam String userId,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws NoSuchAlgorithmException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        CookieUtils.deleteCookie(request, response, "user");
        CookieUtils.deleteCookie(request, response, FOODIE_SHOP_CART);
        return MingzhiJSONResult.ok();

    }

    /**
     * 同步购物车数据到redis和cookie
     *
     * @param userId   用户id
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */

    private void syncShopCartToCookieRedis(String userId, HttpServletRequest request, HttpServletResponse response) {
        String shopCartJSONRedis = redisOperator.get(FOODIE_SHOP_CART + ":" + userId);
        String shopCartCookie = CookieUtils.getCookieValue(request, FOODIE_SHOP_CART, true);
        if (StringUtils.isBlank(shopCartJSONRedis)) {
            if (StringUtils.isNotBlank(shopCartCookie)) {
                redisOperator.set(FOODIE_SHOP_CART + ":" + userId, shopCartCookie);
            }
        } else {
            if (StringUtils.isNotBlank(shopCartCookie)) {
                // merge cookie and redis(compare specId)
                //  redis ∪ (cookie ∩ redis (base on cookie compare by specId)))
                // 1. equal
                //    1.1 cookie override redis
                //    1.2 remove cookie
                // 2. not equal no process
                // 3. left cookie added to redis
                List<ShopCartBO> shopCartBORedisList = JsonUtils.jsonToList(shopCartJSONRedis, ShopCartBO.class);
                List<ShopCartBO> shopCartBOCookieList = JsonUtils.jsonToList(shopCartCookie, ShopCartBO.class);
                List<ShopCartBO> toBeDeletedList = new ArrayList<>();
                if (shopCartBORedisList != null) {
                    for (ShopCartBO oscb : shopCartBORedisList) {
                        String redisSpecId = oscb.getSpecId();
                        if (shopCartBOCookieList != null) {
                            for (ShopCartBO iscb : shopCartBOCookieList) {
                                String cookieSpecId = iscb.getSpecId();
                                if (redisSpecId.equals(cookieSpecId)) {
                                    oscb.setBuyCounts(iscb.getBuyCounts());
                                    toBeDeletedList.add(iscb);
                                }
                            }
                        }
                    }
                }
                shopCartBOCookieList.removeAll(toBeDeletedList);
                shopCartBORedisList.addAll(shopCartBOCookieList);

                redisOperator.set(FOODIE_SHOP_CART + ":" + userId, JsonUtils.objectToJson(shopCartBORedisList));
                CookieUtils.setCookie(request, response, FOODIE_SHOP_CART, JsonUtils.objectToJson(shopCartBORedisList), true);

            } else {
                CookieUtils.setCookie(request, response, FOODIE_SHOP_CART, shopCartJSONRedis, true);
            }
        }


    }


}