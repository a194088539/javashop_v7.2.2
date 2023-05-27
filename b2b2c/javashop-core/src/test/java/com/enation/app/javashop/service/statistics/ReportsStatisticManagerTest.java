package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.statistics.enums.RegionsDataType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 商家中心，运营报告管理业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class ReportsStatisticManagerTest {

    @Autowired
    private ReportsStatisticsManager reportsStatisticsManager;

    @Test
    public void countShop() {

        System.out.println(reportsStatisticsManager.getSalesMoney(getSearchCriteria1()));
        System.out.println(reportsStatisticsManager.getSalesMoney(getSearchCriteria2()));
        System.out.println(reportsStatisticsManager.getSalesMoney(getSearchCriteria3()));
    }

    @Test
    public void getSalesNum() {

        System.out.println(reportsStatisticsManager.getSalesNum(getSearchCriteria1()));
        System.out.println(reportsStatisticsManager.getSalesNum(getSearchCriteria2()));
        System.out.println(reportsStatisticsManager.getSalesNum(getSearchCriteria3()));
    }

    @Test
    public void getSalesPage() {

        System.out.println(reportsStatisticsManager.getSalesPage(getSearchCriteria1(), 1l, 10l));
        System.out.println(reportsStatisticsManager.getSalesPage(getSearchCriteria2(), 1l, 10l));
        System.out.println(reportsStatisticsManager.getSalesPage(getSearchCriteria3(), 1l, 10l));
    }

    @Test
    public void getSalesSummary() {

        System.out.println(reportsStatisticsManager.getSalesSummary(getSearchCriteria1()));
        System.out.println(reportsStatisticsManager.getSalesSummary(getSearchCriteria2()));
        System.out.println(reportsStatisticsManager.getSalesSummary(getSearchCriteria3()));
    }

    @Test
    public void regionsMap() {


        String type1 = RegionsDataType.ORDER_MEMBER_NUM.value();
        String type2 = RegionsDataType.ORDER_PRICE.value();
        String type3 = RegionsDataType.ORDER_NUM.value();

        List list1 = reportsStatisticsManager.regionsMap(getSearchCriteria3(), type1);
        List list2 = reportsStatisticsManager.regionsMap(getSearchCriteria3(), type2);
        List list3 = reportsStatisticsManager.regionsMap(getSearchCriteria3(), type3);
        List list4 = reportsStatisticsManager.regionsMap(getSearchCriteria1(), type3);
        List list5 = reportsStatisticsManager.regionsMap(getSearchCriteria2(), type3);

        System.out.println(list1);
        System.out.println(list2);
        System.out.println(list3);
        System.out.println(list4);
        System.out.println(list5);
    }

    @Test
    public void orderDistribution() {

        Integer[] prices = {0, 10, 20};

        System.out.println(reportsStatisticsManager.orderDistribution(getSearchCriteria1(), prices));
        System.out.println(reportsStatisticsManager.orderDistribution(getSearchCriteria2(), prices));
        System.out.println(reportsStatisticsManager.orderDistribution(getSearchCriteria3(), prices));
    }

    @Test
    public void purchasePeriod() {

        System.out.println(reportsStatisticsManager.purchasePeriod(getSearchCriteria1()));
        System.out.println(reportsStatisticsManager.purchasePeriod(getSearchCriteria2()));
        System.out.println(reportsStatisticsManager.purchasePeriod(getSearchCriteria3()));
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
