package com.mingzhi.interceptor;

import com.mingzhi.utils.JsonUtils;
import com.mingzhi.utils.MingzhiJSONResult;
import com.mingzhi.utils.RedisOperator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

// 用户token拦截
public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    @Autowired
    private RedisOperator redisOperator;

    // 利用前端请求头中包含的headerUserId和headerUserToken来获取userId,userToken
    // 从而和redis中保存的用户userToken比对，如果一致则是同一个用户，反之是伪造用户
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (!Objects.equals(uniqueToken, userToken)) {
                returnErrorResponse(response, MingzhiJSONResult.errorMsg("请登录"));
                return false;
            } else {
                return true;
            }
        } else {
            returnErrorResponse(response, MingzhiJSONResult.errorMsg("请登录"));
            return false;
        }
    }

    public void returnErrorResponse(HttpServletResponse response, MingzhiJSONResult mingzhiJSONResult) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/json");
        try (OutputStream out = response.getOutputStream()) {
            out.write(
                    Objects.requireNonNull(JsonUtils.objectToJson(mingzhiJSONResult))
                            .getBytes(StandardCharsets.UTF_8));
        }

    }

    /**
     * @param request      current HTTP request
     * @param response     current HTTP response
     * @param handler      the handler (or {@link HandlerMethod}) that started asynchronous
     *                     execution, for type and/or instance examination
     * @param modelAndView the {@code ModelAndView} that the handler returned
     *                     (can also be {@code null})
     * @throws Exception in case of errors
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  the handler (or {@link HandlerMethod}) that started asynchronous
     *                 execution, for type and/or instance examination
     * @param ex       any exception thrown on handler execution, if any; this does not
     *                 include exceptions that have been handled through an exception resolver
     * @throws Exception in case of errors
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
