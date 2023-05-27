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
 * 会员相关统计业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class MemberStatisticManagerTest {

    @Autowired
    private MemberStatisticManager memberStatisticManager;

    @Test
    public void getIncreaseMember() {

        System.out.println(memberStatisticManager.getIncreaseMember(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getIncreaseMember(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getIncreaseMember(getSearchCriteria3()));
    }

    @Test
    public void getIncreaseMemberPage() {

        System.out.println(memberStatisticManager.getIncreaseMemberPage(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getIncreaseMemberPage(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getIncreaseMemberPage(getSearchCriteria3()));
    }

    @Test
    public void getMemberOrderQuantity() {

        System.out.println(memberStatisticManager.getMemberOrderQuantity(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getMemberOrderQuantity(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getMemberOrderQuantity(getSearchCriteria3()));
    }

    @Test
    public void getMemberOrderQuantityPage() {

        System.out.println(memberStatisticManager.getMemberOrderQuantityPage(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getMemberOrderQuantityPage(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getMemberOrderQuantityPage(getSearchCriteria3()));
    }

    @Test
    public void getMemberGoodsNum() {

        System.out.println(memberStatisticManager.getMemberGoodsNum(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getMemberGoodsNum(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getMemberGoodsNum(getSearchCriteria3()));
    }

    @Test
    public void getMemberGoodsNumPage() {

        System.out.println(memberStatisticManager.getMemberGoodsNumPage(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getMemberGoodsNumPage(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getMemberGoodsNumPage(getSearchCriteria3()));
    }

    @Test
    public void getMemberMoney() {

        System.out.println(memberStatisticManager.getMemberMoney(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getMemberMoney(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getMemberMoney(getSearchCriteria3()));
    }

    @Test
    public void getMemberMoneyPage() {

        System.out.println(memberStatisticManager.getMemberMoneyPage(getSearchCriteria1()));
        System.out.println(memberStatisticManager.getMemberMoneyPage(getSearchCriteria2()));
        System.out.println(memberStatisticManager.getMemberMoneyPage(getSearchCriteria3()));
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
