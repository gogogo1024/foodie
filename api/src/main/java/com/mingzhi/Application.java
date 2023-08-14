package com.mingzhi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

// 扫描mybatis通用mapper所在包
@MapperScan(basePackages = "com.mingzhi.mapper")
// 扫描所有包com.mingzhi以及组件包org.n3r.idworker
@ComponentScan(basePackages = {"com.mingzhi", "org.n3r.idworker"})
// 排除SecurityAutoConfiguration类，就不需要输入认证
//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})

@SpringBootApplication()
@EnableScheduling // 开启定时任务
//@EnableRedisHttpSession // 开启基于redis来管理spring中http的session
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
