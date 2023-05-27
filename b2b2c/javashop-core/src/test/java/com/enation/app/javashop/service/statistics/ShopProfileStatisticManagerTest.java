package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.statistics.ShopProfileStatisticsMapper;
import com.enation.app.javashop.model.statistics.vo.ShopProfileVO;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 商家中心，店铺概况业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class ShopProfileStatisticManagerTest {

    @Autowired
    private ShopProfileStatisticsManager shopProfileStatisticsManager;

    @Autowired
    ShopProfileStatisticsMapper shopProfileStatisticsMapper;

    @Test
    public void data() {

        ShopProfileVO data = shopProfileStatisticsManager.data();

        System.out.println(data);
    }

    @Test
    public void chart() {

        SimpleChart chart = shopProfileStatisticsManager.chart();

        System.out.println(chart);
    }


}
