package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单相关统计业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class OrderStatisticManagerTest {

    @Autowired
    private OrderStatisticManager orderStatisticManager;

    @Test
    public void getIncreaseMember() {

        String orderStatus = OrderStatusEnum.COMPLETE.value();

        System.out.println(orderStatisticManager.getOrderMoney(getSearchCriteria1(), orderStatus));
        System.out.println(orderStatisticManager.getOrderMoney(getSearchCriteria2(), orderStatus));
        System.out.println(orderStatisticManager.getOrderMoney(getSearchCriteria3(), orderStatus));
    }

    @Test
    public void getOrderPage() {

        String orderStatus = OrderStatusEnum.COMPLETE.value();

        System.out.println(orderStatisticManager.getOrderPage(getSearchCriteria1(), orderStatus, 1l, 10l));
        System.out.println(orderStatisticManager.getOrderPage(getSearchCriteria2(), orderStatus, 1l, 10l));
        System.out.println(orderStatisticManager.getOrderPage(getSearchCriteria3(), orderStatus, 1l, 10l));
    }

    @Test
    public void getOrderNum() {

        String orderStatus = OrderStatusEnum.COMPLETE.value();

        System.out.println(orderStatisticManager.getOrderNum(getSearchCriteria1(), orderStatus));
        System.out.println(orderStatisticManager.getOrderNum(getSearchCriteria2(), orderStatus));
        System.out.println(orderStatisticManager.getOrderNum(getSearchCriteria3(), orderStatus));
    }

    @Test
    public void getSalesMoney() {

        System.out.println(orderStatisticManager.getSalesMoney(getSearchCriteria1(), 1l, 10l));
        System.out.println(orderStatisticManager.getSalesMoney(getSearchCriteria2(), 1l, 10l));
        System.out.println(orderStatisticManager.getSalesMoney(getSearchCriteria3(), 1l, 10l));
    }

    @Test
    public void getAfterSalesMoney() {

        System.out.println(orderStatisticManager.getAfterSalesMoney(getSearchCriteria1(), 1l, 10l));
        System.out.println(orderStatisticManager.getAfterSalesMoney(getSearchCriteria2(), 1l, 10l));
        System.out.println(orderStatisticManager.getAfterSalesMoney(getSearchCriteria3(), 1l, 10l));
    }

    @Test
    public void getSalesMoneyTotal() {

        System.out.println(orderStatisticManager.getSalesMoneyTotal(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getSalesMoneyTotal(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getSalesMoneyTotal(getSearchCriteria3()));
    }

    @Test
    public void getOrderRegionMember() {

        System.out.println(orderStatisticManager.getOrderRegionMember(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getOrderRegionMember(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getOrderRegionMember(getSearchCriteria3()));
    }

    @Test
    public void getOrderRegionNum() {

        System.out.println(orderStatisticManager.getOrderRegionNum(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getOrderRegionNum(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getOrderRegionNum(getSearchCriteria3()));
    }

    @Test
    public void getOrderRegionMoney() {

        System.out.println(orderStatisticManager.getOrderRegionMoney(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getOrderRegionMoney(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getOrderRegionMoney(getSearchCriteria3()));
    }

    @Test
    public void getOrderRegionForm() {

        System.out.println(orderStatisticManager.getOrderRegionForm(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getOrderRegionForm(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getOrderRegionForm(getSearchCriteria3()));
    }

    @Test
    public void getUnitPrice() {

        Integer[] prices = {0, 10, 20};

        System.out.println(orderStatisticManager.getUnitPrice(getSearchCriteria1(), prices));
        System.out.println(orderStatisticManager.getUnitPrice(getSearchCriteria2(), prices));
        System.out.println(orderStatisticManager.getUnitPrice(getSearchCriteria3(), prices));
    }

    @Test
    public void getUnitNum() {

        System.out.println(orderStatisticManager.getUnitNum());
    }

    @Test
    public void getUnitTime() {

        System.out.println(orderStatisticManager.getUnitTime(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getUnitTime(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getUnitTime(getSearchCriteria3()));
    }

    @Test
    public void getReturnMoney() {

        System.out.println(orderStatisticManager.getReturnMoney(getSearchCriteria1()));
        System.out.println(orderStatisticManager.getReturnMoney(getSearchCriteria2()));
        System.out.println(orderStatisticManager.getReturnMoney(getSearchCriteria3()));
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
