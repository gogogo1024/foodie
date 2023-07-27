package com.mingzhi.config;

import com.mingzhi.interceptor.UserTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.time.Duration;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(20000)) // 调试使用20s
                .setReadTimeout(Duration.ofSeconds(20000))
                .build();
    }


    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     *
     * @param registry
     * @see ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String fileSpace = "../" + env.getProperty("baseCfg.fileUploadUrl") + "/";
        fileSpace = fileSpace.replace("/", File.separator);
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resource/")
                .addResourceLocations("file:" + fileSpace + File.separator);

    }

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     * 注册UserTokenInterceptor拦截器
     *
     * @return UserTokenInterceptor拦截器
     */
    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor())
                .addPathPatterns("/hello")
                .addPathPatterns("/shopcart/add")
                .addPathPatterns("/shopcart/del")
                .addPathPatterns("/address/list")
                .addPathPatterns("/address/add")
                .addPathPatterns("/address/update")
                .addPathPatterns("/address/setDefault")
                .addPathPatterns("/address/delete")
                .addPathPatterns("/orders/*")
                .addPathPatterns("/center/*")
                .addPathPatterns("/userInfo/*")
                .addPathPatterns("/myorders/*")
                .addPathPatterns("/mycomments/*")
                .excludePathPatterns("/myorders/deliver")
                .excludePathPatterns("/orders/notifyMerchantOrderPaid");
        ;
        WebMvcConfigurer.super.addInterceptors(registry);

    }
}
