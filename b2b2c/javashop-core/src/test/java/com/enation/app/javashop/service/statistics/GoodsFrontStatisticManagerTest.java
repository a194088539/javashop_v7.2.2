package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.base.SearchCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * 商品分析业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class GoodsFrontStatisticManagerTest {

    @Autowired
    private GoodsFrontStatisticsManager goodsFrontStatisticsManager;

    @Test
    public void getGoodsDetail() {
        long catId1 = 0;
        long catId2 = 1l;
        long catId3 = 4l;

        WebPage goodsDetail1 = goodsFrontStatisticsManager.getGoodsDetail(1l, 3l, catId1, null);
        WebPage goodsDetail2 = goodsFrontStatisticsManager.getGoodsDetail(1l, 3l, catId2, null);
        WebPage goodsDetail3 = goodsFrontStatisticsManager.getGoodsDetail(1l, 3l, catId3, null);

        System.out.println(goodsDetail1);
        System.out.println(goodsDetail2);
        System.out.println(goodsDetail3);
    }

    @Test
    public void getGoodsPriceSales() {

        System.out.println(goodsFrontStatisticsManager.getGoodsPriceSales(Arrays.asList(0, 100, 200), getSearchCriteria1()));
        System.out.println(goodsFrontStatisticsManager.getGoodsPriceSales(Arrays.asList(0, 100, 200), getSearchCriteria2()));
        System.out.println(goodsFrontStatisticsManager.getGoodsPriceSales(Arrays.asList(0, 100, 200), getSearchCriteria3()));

    }
    
    @Test
    public void getGoodsOrderPriceTopPage() {

        System.out.println(goodsFrontStatisticsManager.getGoodsOrderPriceTopPage(0, getSearchCriteria1()));
        System.out.println(goodsFrontStatisticsManager.getGoodsOrderPriceTopPage(0, getSearchCriteria2()));
        System.out.println(goodsFrontStatisticsManager.getGoodsOrderPriceTopPage(0, getSearchCriteria3()));
    }

    @Test
    public void getGoodsNumTopPage() {

        System.out.println(goodsFrontStatisticsManager.getGoodsNumTopPage(0, getSearchCriteria1()));
        System.out.println(goodsFrontStatisticsManager.getGoodsNumTopPage(0, getSearchCriteria2()));
        System.out.println(goodsFrontStatisticsManager.getGoodsNumTopPage(0, getSearchCriteria3()));
    }

    @Test
    public void getGoodsOrderPriceTop() {

        System.out.println(goodsFrontStatisticsManager.getGoodsOrderPriceTop(0, getSearchCriteria1()));
        System.out.println(goodsFrontStatisticsManager.getGoodsOrderPriceTop(0, getSearchCriteria2()));
        System.out.println(goodsFrontStatisticsManager.getGoodsOrderPriceTop(0, getSearchCriteria3()));
    }

    @Test
    public void getGoodsNumTop() {

        System.out.println(goodsFrontStatisticsManager.getGoodsNumTop(0, getSearchCriteria1()));
        System.out.println(goodsFrontStatisticsManager.getGoodsNumTop(0, getSearchCriteria2()));
        System.out.println(goodsFrontStatisticsManager.getGoodsNumTop(0, getSearchCriteria3()));
    }


    private SearchCriteria getSearchCriteria1(){
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCycleType("YEAR");
        searchCriteria.setYear(2019);
        searchCriteria.setMonth(12);
        searchCriteria.setCategoryId(0l);

        return searchCriteria;
    }

    private SearchCriteria getSearchCriteria2(){
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCycleType("YEAR");
        searchCriteria.setYear(2020);
        searchCriteria.setMonth(12);
        searchCriteria.setCategoryId(555l);

        return searchCriteria;
    }

    private SearchCriteria getSearchCriteria3(){
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setCycleType("YEAR");
        searchCriteria.setYear(2020);
        searchCriteria.setMonth(12);
        searchCriteria.setCategoryId(0l);

        return searchCriteria;
    }

}
