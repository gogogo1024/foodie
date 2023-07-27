package com.mingzhi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

// 扫描mybatis通用mapper所在包
@MapperScan(basePackages = "com.mingzhi.mapper")
// 扫描所有包com.mingzhi以及组件包org.n3r.idworker
@ComponentScan(basePackages = {"com.mingzhi", "org.n3r.idworker"})
// 排除SecurityAutoConfiguration类，就不需要输入认证
@SpringBootApplication()
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
