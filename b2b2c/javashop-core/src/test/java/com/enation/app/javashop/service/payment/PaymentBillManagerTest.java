package com.enation.app.javashop.service.payment;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.payment.PaymentBillMapper;
import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 支付帐单业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class PaymentBillManagerTest {

    @Autowired
    private PaymentBillManager paymentBillManager;

    @Autowired
    private PaymentBillMapper paymentBillMapper;


    @Test
    public void list() {

        WebPage list = paymentBillManager.list(1, 10);
        System.out.println(list);
    }

    @Test
    public void getByBillSn() {

        PaymentBillDO bill_sn = new QueryChainWrapper<>(paymentBillMapper).eq("bill_sn", "29416667834896394").one();

        System.out.println(bill_sn);
    }

    @Test
    public void getBillByReturnTradeNo() {

        PaymentBillDO bill_sn = paymentBillManager.getBillByReturnTradeNo("1");

        System.out.println(bill_sn);
    }

    @Test
    public void edit() {

        PaymentBillDO paymentBillDO = new PaymentBillDO();
        paymentBillDO.setReturnTradeNo("axxa");

        paymentBillManager.edit(paymentBillDO, "29416667834896394");
    }

    @Test
    public void getBySubSnAndServiceType() {

        PaymentBillDO bySubSnAndServiceType = paymentBillManager.getBySubSnAndServiceType("29416648620527617", "RECHARGE");

        System.out.println(bySubSnAndServiceType);

        PaymentBillDO bySubSnAndServiceType1 = paymentBillManager.getBySubSnAndServiceType("29416648620527617", "TRADE");

        System.out.println(bySubSnAndServiceType1);
    }

    @Test
    public void paySuccess() {

        new UpdateChainWrapper<>(paymentBillMapper)
                .set("is_pay", 1)
                .set("return_trade_no", "哈哈")
                .eq("bill_id", 29416667843022859l)
                .update();
    }

}
