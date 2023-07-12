package com.mingzhi.config;

import com.mingzhi.controller.OrdersController;
import com.mingzhi.service.OrderService;
import com.mingzhi.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时关闭订单
 * TODO 单机状态下会有三个问题
 * 1. 时间差，10:10下单，11:00检查不足1小时，12:00检查时已经超过1小时了
 *    - 10:10下单，必然会需要有11:10的检查动作，对订单状态查询后，看用户是否支付（延时队列）
 *    - 如果未支付，关闭订单，反之则不做处理
 * 2. 不支持集群，任务会被多次执行
 *    - 一台机器专用于运行所有定时任务
 * 3. 数据量大的时候，等同于全表扫描
 *    - 把不同订单状态的表分开，直接查询固定表
 */
@Component
public class OrderJob {
    final static Logger logger = LoggerFactory.getLogger(OrdersController.class);
    @Autowired
    private OrderService orderService;


    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoClose() {
        orderService.closeOrder();
        logger.info("执行订单关闭任务,当前时间为: {} ", DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN));

    }
}

