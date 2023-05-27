package com.enation.app.javashop.service.promotion.seckill;

import com.enation.app.javashop.framework.test.TestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *  限时抢购时刻业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class SeckillRangeManagerTest {

    @Autowired
    private SeckillRangeManager seckillRangeManager;

    @Test
    public void list() {

        System.out.println(seckillRangeManager.list(1l, 10l));
    }

    @Test
    public void readTimeList() {

        System.out.println(seckillRangeManager.readTimeList());
    }


}
