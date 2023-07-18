package com.mingzhi;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 打包war的启动累
 */
public class WarStarterApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder springApplicationBuilder) {
        // 指向自己的应用启动类
        return springApplicationBuilder.sources(Application.class);
    }
}
