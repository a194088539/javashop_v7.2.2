package com.enation.app.javashop.service.trade.order.plugin;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 商家中心，商品收藏统计业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderServicePluginTest {

    @Autowired
    private OrderServicePlugin orderServicePlugin;

    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void getPrice() {

        System.out.println(orderServicePlugin.getPrice("37419452492709890"));
    }

    @Test
    public void updatePaymentMethod() {

        orderServicePlugin.updatePaymentMethod("37419452492709890","1","2");
    }

    @Test
    public void balancePay() {

        new UpdateChainWrapper<>(orderMapper)
                //设置应付金额
                .setSql(" need_pay_money = need_pay_money - " + 1)
                //设置支付插件id为balancePayPlugin
                .set("payment_plugin_id", "balancePayPlugin")
                //设置支付方式为预存款
                .set("payment_method_name", "预存款")
                //设置预存款抵扣金额
                .setSql("balance = balance + " + 1)
                //设置支付时间
                .set("payment_time", 0)
                //按订单编号修改
                .eq("sn", "37419452492709890")
                //提交修改
                .update();
    }

}
