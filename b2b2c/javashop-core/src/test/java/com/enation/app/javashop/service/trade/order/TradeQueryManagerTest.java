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
 * 交易查询业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class TradeQueryManagerTest {

    @Autowired
    private TradeQueryManager tradeQueryManager;

    @Test
    public void getModel() {

        System.out.println(tradeQueryManager.getModel("37419452433858561"));

    }

    @Test
    public void checkIsOwner() {

        tradeQueryManager.checkIsOwner("37419452433858561", 16l);
    }

}
