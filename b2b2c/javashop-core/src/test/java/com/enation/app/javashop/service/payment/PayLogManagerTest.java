package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.payment.PayLogMapper;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * 收款单业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class PayLogManagerTest {

    @Autowired
    private PayLogManager payLogManager;

    @Autowired
    private PayLogMapper payLogMapper;

    @Test
    public void add() {

        PayLog payLog = new PayLog();
        payLog.setOrderSn("xxxxxxaaaa");

        payLogManager.add(payLog);
    }

    @Test
    public void edit() {

        PayLog payLog = new PayLog();
        payLog.setOrderSn("xxxxxxaaaa哈哈");

        payLogManager.edit(payLog, 1288403695152320513l);
    }


    @Test
    public void delete() {

        payLogManager.delete(1288403695152320513l);
    }

    @Test
    public void getModel() {

        PayLog model = payLogManager.getModel(24418821716742168l);
        System.out.println(model);
    }

    @Test
    public void getModel1() {

        PayLog model = payLogManager.getModel("29046245100371970");
        System.out.println(model);
    }

}
