package com.mingzhi;

import com.mingzhi.component.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSender() throws InterruptedException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("age", 33);
        properties.put("name", "gogogo1024");
        rabbitSender.send("message from sender", properties);
        Thread.sleep(10000);
    }
}
