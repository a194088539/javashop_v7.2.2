package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.base.SearchCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *  商家中心与平台后台，流量分析业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class PageViewStatisticManagerTest {

    @Autowired
    private PageViewStatisticManager pageViewStatisticManager;

    @Test
    public void countShop() {

        System.out.println(pageViewStatisticManager.countShop(getSearchCriteria1()));
        System.out.println(pageViewStatisticManager.countShop(getSearchCriteria2()));
        System.out.println(pageViewStatisticManager.countShop(getSearchCriteria3()));
    }

    @Test
    public void countGoods() {

        System.out.println(pageViewStatisticManager.countGoods(getSearchCriteria1()));
        System.out.println(pageViewStatisticManager.countGoods(getSearchCriteria2()));
        System.out.println(pageViewStatisticManager.countGoods(getSearchCriteria3()));
    }


    private SearchCriteria getSearchCriteria1(){
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCycleType("YEAR");
        searchCriteria.setYear(2019);
        searchCriteria.setMonth(12);
        searchCriteria.setCategoryId(0l);
        searchCriteria.setSellerId(17l);

        return searchCriteria;
    }

    private SearchCriteria getSearchCriteria2(){
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCycleType("YEAR");
        searchCriteria.setYear(2020);
        searchCriteria.setMonth(12);
        searchCriteria.setCategoryId(555l);
        searchCriteria.setSellerId(17l);

        return searchCriteria;
    }

    private SearchCriteria getSearchCriteria3(){
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCycleType("YEAR");
        searchCriteria.setYear(2020);
        searchCriteria.setMonth(12);
        searchCriteria.setCategoryId(0l);
        searchCriteria.setSellerId(17l);

        return searchCriteria;
    }

}
