package com.mingzhi.controller;

import com.mingzhi.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


@ApiIgnore()
@RestController()
@RequestMapping("redis")
public class RedisController {
    final static Logger logger = LoggerFactory.getLogger(RedisController.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisOperator redisOperator;


    @GetMapping("/set")
    public String set(String key, String val) {
        redisOperator.set(key, val);
        return "ok";
    }

    @GetMapping("/get")
    public String get(String key) {
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public String delete(String key) {
        redisOperator.del(key);
        return "ok";
    }
}
