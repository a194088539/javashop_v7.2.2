package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.payment.PaymentMethodMapper;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付方式业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class PaymentMethodManagerTest {

    @Autowired
    private PaymentMethodManager paymentMethodManager;
    @Autowired
    private PaymentMethodMapper paymentMethodMapper;

    @Test
    public void add() {
        Map delMap = new HashMap();
        delMap.put("plugin_id", "xxx");
        paymentMethodMapper.deleteByMap(delMap);
    }

    @Test
    public void delete() {
        paymentMethodMapper.deleteById(1l);
    }

    @Test
    public void getByPluginId() {

        PaymentMethodDO weixinPayPlugin = paymentMethodManager.getByPluginId("weixinPayPlugin");

        System.out.println(weixinPayPlugin);
    }

    @Test
    public void queryMethodByClient() {

        List<Map<String,Object>> list = paymentMethodMapper.selectMaps(null);

        System.out.println(list);
    }

    @Test
    public void getConfig() {

        String s = paymentMethodMapper.selectClientType("pc_config", "weixinPayPlugin");

        System.out.println(s);
    }

}
