package com.mingzhi.controller;

import com.mingzhi.pojo.Users;
import com.mingzhi.pojo.vo.UsersVO;
import com.mingzhi.service.UserService;
import com.mingzhi.utils.JsonUtils;
import com.mingzhi.utils.MD5Utils;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.RedisOperator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;


@Controller()
public class SSOController {
    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_USER_TICKET = "redis_tmp_user_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";
    final static Logger logger = LoggerFactory.getLogger(SSOController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/login")
    public String login(String returnUrl, Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password,
                          String returnUrl, Model model,
                          HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException {

        if (StringUtils.isBlank(username)
                || StringUtils.isBlank(password)
        ) {
            model.addAttribute("errmsg", "用户名或者密码不能空");
            return "login";

        }
        Users user = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (user == null) {
            model.addAttribute("errmsg", "用户名或者密码不能空");
            return "login";
        }

        // 用户会话和redis绑定
        String uniqueToken = UUID.randomUUID().toString().trim();


        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + user.getId(),
                JsonUtils.objectToJson(usersVO));

        // 生成全局ticket,代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();

        // 全局ticket 存入sso端cookie
        setCookie(COOKIE_USER_TICKET, userTicket, response);
        redisOperator.set(REDIS_USER_TICKET + ":" + user.getId(),
                userTicket);
        String tmpTicket = createTmpTicket();
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;

    }

    @PostMapping("verifyTmpTicket")
    @ResponseBody
    public MingzhiJSONResult verifyTmpTicket(String tmpTicket,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws NoSuchAlgorithmException {
        String tmpTicketVal = redisOperator.get(REDIS_TMP_USER_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketVal)) {
            return MingzhiJSONResult.errorUserTicket("用户ticket异常");
        } else {
            if (!tmpTicketVal.equals(MD5Utils.getMD5Str(tmpTicket))) {
                return MingzhiJSONResult.errorUserTicket("用户ticket异常");
            } else {
                redisOperator.del(REDIS_TMP_USER_TICKET + ":" + tmpTicket);
//            }
            }
//            String cookieUserTicket = CookieUtils.getCookieValue(request, COOKIE_USER_TICKET, false);

            String cookieUserTicket = getCookie(request, COOKIE_USER_TICKET);
            String userId = redisOperator.get(REDIS_USER_TICKET + ":" + cookieUserTicket);
            if (StringUtils.isBlank(userId)) {
                return MingzhiJSONResult.errorUserTicket("用户ticket异常");
            }
            String redisUserInfo = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(redisUserInfo)) {
                return MingzhiJSONResult.errorUserTicket("用户ticket异常");
            }
            return MingzhiJSONResult.ok(JsonUtils.jsonToPojo(redisUserInfo, UsersVO.class));
        }
    }


    private String createTmpTicket() throws NoSuchAlgorithmException {
        String tmpTicket = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_TMP_USER_TICKET + ":" + tmpTicket,
                MD5Utils.getMD5Str(tmpTicket), 600);
        return tmpTicket;
    }

    private void setCookie(String key, String val, HttpServletResponse response) {
        Cookie cookie = new Cookie(key, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request, String key) {

        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || StringUtils.isBlank(key)) {
            return null;
        }

        String cookieValue = null;
        for (Cookie cookie : cookieList) {
            if (cookie.getName().equals(key)) {
                cookieValue = cookie.getValue();
                break;
            }
        }

        return cookieValue;
    }

}