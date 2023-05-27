package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.trade.order.enums.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单出库状态业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderOutStatusManagerTest {

    @Autowired
    private OrderOutStatusManager orderOutStatusManager;

    @Test
    public void updateServiceStatus() {

        System.out.println(orderOutStatusManager.list(1l, 10l));
    }

    @Test
    public void edit() {

        orderOutStatusManager.edit("37419452492709890", OrderOutTypeEnum.GOODS, OrderOutStatusEnum.FAIL);
    }


    @Test
    public void getModel() {

        System.out.println(orderOutStatusManager.getModel("37419452492709890", OrderOutTypeEnum.GOODS));
    }



}
