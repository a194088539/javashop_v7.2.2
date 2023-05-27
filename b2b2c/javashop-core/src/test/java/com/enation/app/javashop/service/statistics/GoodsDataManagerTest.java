package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.model.statistics.dto.GoodsData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 商品收集业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class GoodsDataManagerTest {

    @Autowired
    private GoodsDataManager goodsDataManager;

    @Test
    public void deleteGoods() {

        Long[] goodIds = {123l, 456l, 3l, 4l};

        goodsDataManager.deleteGoods(goodIds);
    }

    @Test
    public void updateCollection() {

        GoodsData goodsData = new GoodsData();
        goodsData.setGoodsId(29043976057270273l);
        goodsData.setFavoriteNum(5);

        goodsDataManager.updateCollection(goodsData);
    }

    @Test
    public void get() {

        GoodsData goodsData = goodsDataManager.get(29043976057270273l);

        System.out.println(goodsData);
    }

    @Test
    public void underAllGoods() {

        goodsDataManager.underAllGoods(18l);
    }

}
