package com.mingzhi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    public CorsConfig() {

    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedOrigin("http://www.mtv.com");
        corsConfiguration.addAllowedOrigin("http://www.music.com");
        corsConfiguration.addAllowedOrigin("http://www.mtv.com:8080");
        corsConfiguration.addAllowedOrigin("http://www.music.com:8080");

        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true); //发送是否携带cookie信息
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(corsConfigurationSource);


    }
}
