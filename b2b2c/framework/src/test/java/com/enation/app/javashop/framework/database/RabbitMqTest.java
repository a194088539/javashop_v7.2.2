package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.FrameworkApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by kingapex on 2018/10/17.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/10/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
@ComponentScan("com.enation.app.javashop")
public class RabbitMqTest {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public static  final String  KEY = "TEST_KEY";


    @Test
    public  void test() throws InterruptedException {

        for (int i=0;i<100;i++) {
            this.amqpTemplate.convertAndSend(KEY, KEY + "_ROUTING", "mymeessage"+i);
        }
        Thread.sleep(10000);

    }



}
