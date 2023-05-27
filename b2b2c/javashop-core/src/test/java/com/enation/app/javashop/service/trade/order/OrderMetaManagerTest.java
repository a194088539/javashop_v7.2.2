package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderMetaManagerTest {

    @Autowired
    private OrderMetaManager orderMetaManager;

    @Test
    public void getMetaValue() {

        System.out.println(orderMetaManager.getMetaValue("37419452492709890", OrderMetaKeyEnum.CASH_BACK));
    }

    @Test
    public void list() {

        System.out.println(orderMetaManager.list("37419452492709890"));
    }

    @Test
    public void updateMetaValue() {

        orderMetaManager.updateMetaValue("37419452492709890", OrderMetaKeyEnum.CASH_BACK, "111");
    }

    @Test
    public void updateMetaStatus() {

        orderMetaManager.updateMetaStatus("37419452492709890", OrderMetaKeyEnum.CASH_BACK, "0");
    }

    @Test
    public void getModel() {

        System.out.println(orderMetaManager.getModel("37419452492709890", OrderMetaKeyEnum.CASH_BACK));
    }

    @Test
    public void getGiftList() {

        System.out.println(orderMetaManager.getGiftList("37419452492709890", "0"));
    }

}
