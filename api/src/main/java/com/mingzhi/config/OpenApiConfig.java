package com.mingzhi.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * api文档配置
 */
@Configuration
public class OpenApiConfig {
    // TODO springdoc swagger文档目前集成有问题
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(generateInfo());
    }

    private Info generateInfo() {
        return new Info().title("吃货项目文档")
                .description("foodie项目api文档")
                .termsOfService("https://google.com/")
                .version("1.0.1")
                .license(license())
                .contact(contact());


    }

    private Contact contact() {
        return new Contact()
                .name("gogogo1024")                             // 作者名称
                .email("jxycbjhc@gmail.com")                   // 作者邮箱
                .url("https://github.com/gogogo1024")  // 介绍作者的URL地址
                .extensions(new HashMap<String, Object>()); // 使用Map配置信息（如key为"name","email","url"）
    }

    private License license() {
        return new License()
                .name("Apache 2.0")                         // 授权名称
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")    // 授权信息
                .extensions(new HashMap<String, Object>());// 使用Map配置信息（如key为"name","url","identifier"
    }

}

