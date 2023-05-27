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
 * 商品相关统计业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class GoodsStatisticManagerTest {

    @Autowired
    private GoodsStatisticManager goodsStatisticManager;

    @Test
    public void getPriceSales() {

        Integer[] prices = {0, 10, 20};

        System.out.println(goodsStatisticManager.getPriceSales(getSearchCriteria1(), prices));
        System.out.println(goodsStatisticManager.getPriceSales(getSearchCriteria2(), prices));
        System.out.println(goodsStatisticManager.getPriceSales(getSearchCriteria3(), prices));
    }

    @Test
    public void getHotSalesMoney() {

        System.out.println(goodsStatisticManager.getHotSalesMoney(getSearchCriteria1()));
        System.out.println(goodsStatisticManager.getHotSalesMoney(getSearchCriteria2()));
        System.out.println(goodsStatisticManager.getHotSalesMoney(getSearchCriteria3()));
    }

    @Test
    public void getHotSalesMoneyPage() {

        System.out.println(goodsStatisticManager.getHotSalesMoneyPage(getSearchCriteria1()));
        System.out.println(goodsStatisticManager.getHotSalesMoneyPage(getSearchCriteria2()));
        System.out.println(goodsStatisticManager.getHotSalesMoneyPage(getSearchCriteria3()));
    }

    @Test
    public void getHotSalesNum() {

        System.out.println(goodsStatisticManager.getHotSalesNum(getSearchCriteria1()));
        System.out.println(goodsStatisticManager.getHotSalesNum(getSearchCriteria2()));
        System.out.println(goodsStatisticManager.getHotSalesNum(getSearchCriteria3()));
    }

    @Test
    public void getHotSalesNumPage() {

        System.out.println(goodsStatisticManager.getHotSalesNumPage(getSearchCriteria1()));
        System.out.println(goodsStatisticManager.getHotSalesNumPage(getSearchCriteria2()));
        System.out.println(goodsStatisticManager.getHotSalesNumPage(getSearchCriteria3()));
    }

    @Test
    public void getSaleDetails() {

        System.out.println(goodsStatisticManager.getSaleDetails(getSearchCriteria1(), "", 3l, 1l));
        System.out.println(goodsStatisticManager.getSaleDetails(getSearchCriteria2(), "", 3l, 1l));
        System.out.println(goodsStatisticManager.getSaleDetails(getSearchCriteria3(), "", 3l, 1l));
    }

    @Test
    public void getGoodsCollectPage() {

        System.out.println(goodsStatisticManager.getGoodsCollectPage(getSearchCriteria1()));
        System.out.println(goodsStatisticManager.getGoodsCollectPage(getSearchCriteria2()));
        System.out.println(goodsStatisticManager.getGoodsCollectPage(getSearchCriteria3()));
    }

    @Test
    public void getGoodsCollect() {

        System.out.println(goodsStatisticManager.getGoodsCollect(getSearchCriteria1()));
        System.out.println(goodsStatisticManager.getGoodsCollect(getSearchCriteria2()));
        System.out.println(goodsStatisticManager.getGoodsCollect(getSearchCriteria3()));
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
