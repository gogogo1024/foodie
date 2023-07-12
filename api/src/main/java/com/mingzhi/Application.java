package com.mingzhi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

// 扫描mybatis通用mapper所在包
@MapperScan(basePackages = "com.mingzhi.mapper")
// 扫描所有包以及组件包
@ComponentScan(basePackages = {"com.mingzhi", "org.n3r.idworker"})
@SpringBootApplication()
@EnableScheduling // 开启定时任务
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
