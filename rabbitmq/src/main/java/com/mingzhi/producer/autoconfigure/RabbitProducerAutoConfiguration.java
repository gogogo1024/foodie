package com.mingzhi.producer.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.mingzhi.producer.*")
public class RabbitProducerAutoConfiguration {
}
