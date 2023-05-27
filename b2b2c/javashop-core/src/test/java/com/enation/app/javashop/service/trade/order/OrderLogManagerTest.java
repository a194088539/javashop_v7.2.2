package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.trade.order.dos.OrderLogDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单日志业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderLogManagerTest {

    @Autowired
    private OrderLogManager orderLogManager;

    @Test
    public void list() {

        System.out.println(orderLogManager.list(1l, 10l));
    }

    @Test
    public void listAll() {

        System.out.println(orderLogManager.listAll("37419452492709890"));
    }

    @Test
    public void add() {

        OrderLogDO orderLog = new OrderLogDO();
        orderLog.setOrderSn("xxxxxxxxxxxxxxxxxxxx");

        orderLogManager.add(orderLog);
    }

    @Test
    public void edit() {

        OrderLogDO orderLog = new OrderLogDO();
        orderLog.setOrderSn("xxxxxxxxxxxxxxxxxxxx222");

        orderLogManager.edit(orderLog, 1l);
    }

    @Test
    public void delete() {

        orderLogManager.delete(2l);
    }

    @Test
    public void getModel() {

        System.out.println(orderLogManager.getModel(37419454577143817l));

    }


}
