package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.member.vo.SalesVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 会员销售记录业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class MemberSalesManagerTest {

    @Autowired
    private MemberSalesManager memberSalesManager;

    @Test
    public void list() {

        WebPage<SalesVO> list = memberSalesManager.list(10l, 1l, 29445995079286795l);

        System.out.println(list);
    }


}
