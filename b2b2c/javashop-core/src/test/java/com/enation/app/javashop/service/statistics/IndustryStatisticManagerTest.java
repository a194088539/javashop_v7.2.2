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
 * 后台 行业分析业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class IndustryStatisticManagerTest {

    @Autowired
    private IndustryStatisticManager industryStatisticManager;

    @Test
    public void getOrderQuantity() {

        System.out.println(industryStatisticManager.getOrderQuantity(getSearchCriteria1()));
        System.out.println(industryStatisticManager.getOrderQuantity(getSearchCriteria2()));
        System.out.println(industryStatisticManager.getOrderQuantity(getSearchCriteria3()));
    }

    @Test
    public void getGoodsNum() {

        System.out.println(industryStatisticManager.getGoodsNum(getSearchCriteria1()));
        System.out.println(industryStatisticManager.getGoodsNum(getSearchCriteria2()));
        System.out.println(industryStatisticManager.getGoodsNum(getSearchCriteria3()));
    }

    @Test
    public void getOrderMoney() {

        System.out.println(industryStatisticManager.getOrderMoney(getSearchCriteria1()));
        System.out.println(industryStatisticManager.getOrderMoney(getSearchCriteria2()));
        System.out.println(industryStatisticManager.getOrderMoney(getSearchCriteria3()));
    }

    @Test
    public void getGeneralOverview() {

        System.out.println(industryStatisticManager.getGeneralOverview(getSearchCriteria1()));
        System.out.println(industryStatisticManager.getGeneralOverview(getSearchCriteria2()));
        System.out.println(industryStatisticManager.getGeneralOverview(getSearchCriteria3()));
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
