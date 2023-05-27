package com.enation.app.javashop.service.trade.snapshot;

import com.enation.app.javashop.framework.test.TestConfig;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.model.trade.snapshot.GoodsSnapshot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 订单出库状态业务层测试
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/08/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class GoodsSnapshotManagerTest {

    @Autowired
    private GoodsSnapshotManager goodsSnapshotManager;

    @Test
    public void updateServiceStatus() {
        GoodsSnapshot snapshot = new GoodsSnapshot();
        snapshot.setGoodsId(1L);
        snapshot.setName("xx");
        snapshot.setSn("xx");
        snapshot.setCategoryName("xx");
        snapshot.setBrandName("xx");
        snapshot.setGoodsType("xx");
        snapshot.setHaveSpec(0);
        snapshot.setWeight(0.0);
        snapshot.setIntro("xx");
        snapshot.setPrice(0.0);
        snapshot.setCost(0.0);
        snapshot.setMktprice(0.0);



        snapshot.setParamsJson("xx");
        snapshot.setImgJson("xx");
        snapshot.setPoint(99999);
        snapshot.setSellerId(1L);



        snapshot.setCreateTime(DateUtil.getDateline());
        snapshot.setPromotionJson("xx");
        snapshot.setCouponJson("xx");
        snapshot.setMobileIntro("xx");

        snapshot.setMemberId(1L);
        goodsSnapshotManager.add(snapshot);
    }

}
