package com.mingzhi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
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
                .setReadTimeout(Duration.ofSeconds(20000))   //
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
}
