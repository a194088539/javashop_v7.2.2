package com.enation.app.javashop.api;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.service.promotion.halfprice.HalfPriceManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.test.annotation.Rollback;

/**
 * Author: gy
 * Date: Created in 2020/8/8 3:32 下午
 * Version: 0.0.1
 */


@Rollback(false)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class HalfPriceManagerTest {

    @Autowired
    private HalfPriceManager halfPriceManager;

    @Test
    public void test(){
        System.out.println("add");
        WebPage<HalfPriceVO> list = halfPriceManager.list(1, 10, null);
        HalfPriceVO priceVO = list.getData().get(0);



    }


}
