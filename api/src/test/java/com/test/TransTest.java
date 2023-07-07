package com.test;

import com.mingzhi.Application;
import com.mingzhi.service.StuService;
import com.mingzhi.service.impl.TestTransServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TransTest {
    @Autowired
    private StuService stuService;
    @Autowired
    private TestTransServiceImpl testTransService;

    @Test
    public void myTest() {
        testTransService.testPropagationTrans();
    }
}
