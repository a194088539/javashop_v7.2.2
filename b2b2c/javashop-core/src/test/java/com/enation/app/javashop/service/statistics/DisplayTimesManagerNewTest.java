package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.mapper.statistics.GoodsPageViewMapper;
import com.enation.app.javashop.mapper.statistics.ShopPageViewMapper;
import com.enation.app.javashop.model.statistics.dos.GoodsPageView;
import com.enation.app.javashop.model.statistics.dos.ShopPageView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * 店铺访问量统计业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/05
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class DisplayTimesManagerNewTest  {


    @Autowired
    private ShopPageViewMapper shopPageViewMapper;

    @Autowired
    private GoodsPageViewMapper goodsPageViewMapper;

    /**
     * 批量插入测试
     */
    @Test
    public void test() {

        ShopPageView shopPageView1 = new ShopPageView();
        shopPageView1.setDay(1000);
        ShopPageView shopPageView2 = new ShopPageView();
        shopPageView2.setDay(1001);
        List<ShopPageView> shopPageViewList = Arrays.asList(shopPageView1, shopPageView2);
        for(ShopPageView shopPageView : shopPageViewList){
            shopPageViewMapper.insert(shopPageView);
        }

        GoodsPageView goodsPageView1 = new GoodsPageView();
        goodsPageView1.setGoodsName("xxxxx1");
        GoodsPageView goodsPageView2 = new GoodsPageView();
        goodsPageView2.setGoodsName("xxxxx2");
        List<GoodsPageView> goodsPageViewList = Arrays.asList(goodsPageView1, goodsPageView2);
        for(GoodsPageView goodsPageView : goodsPageViewList){
            goodsPageViewMapper.insert(goodsPageView);
        }

    }
}
