package com.enation.app.javashop.service.trade.order.plugin;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.TradeMapper;
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
public class TradeServicePluginTest {

    @Autowired
    private TradeServicePlugin tradeServicePlugin;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TradeMapper tradeMapper;

    @Test
    public void getPrice() {

        System.out.println(tradeServicePlugin.getPrice("37419452433858561"));
    }

    @Test
    public void balancePay() {

        new UpdateChainWrapper<>(tradeMapper)
                //设置总价格
                .setSql("total_price = total_price - " + 1)
                //设置支付方式名称
                .set("payment_method_name", "预存款")
                //设置预存款抵扣金额
                .setSql("balance = balance + " + 1)
                //按交易编号修改
                .eq("trade_sn", "37419452433858561")
                //提交修改
                .update();

    }

    @Test
    public void paySuccess() {

        new UpdateChainWrapper<>(orderMapper)
                //设置支付方式返回的交易号
                .set("pay_order_no", "xxxx")
                //按交易编号修改
                .eq("trade_sn", "37419452433858561")
                //提交修改
                .update();
    }

}
