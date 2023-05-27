package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
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
public class CollectFrontStatisticsManagerTest {

    @Autowired
    private CollectFrontStatisticsManager collectFrontStatisticsManager;

    @Test
    public void getChart() {

        SimpleChart chart = collectFrontStatisticsManager.getChart(1l);

        System.out.println(chart);
    }

    @Test
    public void getPage() {

        WebPage page = collectFrontStatisticsManager.getPage(1l, 3l, 1l);

        System.out.println(page);
    }

}
