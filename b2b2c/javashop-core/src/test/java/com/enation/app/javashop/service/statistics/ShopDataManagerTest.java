package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.shop.enums.ShopStatusEnum;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 店铺收藏数据管理业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/06
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class ShopDataManagerTest {

    @Autowired
    private ShopDataManager shopDataManager;

    @Test
    public void updateCollection() {

        ShopData shopData = new ShopData();
        shopData.setSellerId(17l);
        shopData.setFavoriteNum(50);

        shopDataManager.updateCollection(shopData);
    }

    @Test
    public void updateShopData() {

        ShopData shopData = new ShopData();
        shopData.setSellerId(29099983504556036l);
        shopData.setFavoriteNum(2);
        shopData.setSellerName("xxx");

        shopDataManager.updateShopData(shopData);
    }

    @Test
    public void add() {

        ShopData shopData = new ShopData();
        shopData.setSellerId(0l);
        shopData.setFavoriteNum(2);
        shopData.setSellerName("xxx");
        shopData.setShopDisable(ShopStatusEnum.CLOSED.value());

        shopDataManager.add(shopData);
    }

    @Test
    public void get() {

        System.out.println(shopDataManager.get(1l));
    }


}
