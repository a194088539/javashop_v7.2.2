package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.test.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单任务业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderTaskManagerTest {

    @Autowired
    private OrderTaskManager orderTaskManager;

    @Test
    public void cancelTask() {

        orderTaskManager.cancelTask();
    }

    @Test
    public void rogTask() {

        orderTaskManager.rogTask();
    }

    @Test
    public void completeTask() {

        orderTaskManager.completeTask();
    }

    @Test
    public void payTask() {

        orderTaskManager.payTask();
    }

    @Test
    public void serviceTask() {

        orderTaskManager.serviceTask();
    }

    @Test
    public void commentTask() {

        orderTaskManager.commentTask();
    }

    @Test
    public void complainTask() {

        orderTaskManager.complainTask();
    }

}
