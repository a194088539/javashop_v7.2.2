package com.enation.app.javashop.api;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Random;

/**
 * 分库分表测试
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/6/10
 */
@Rollback(false)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class ShardTest extends BaseTest {
    @Autowired
    private DaoSupport daoSupport;

    @Autowired
    SnCreator snCreator;



    @Test
    public void snTest() {
        Long sn = snCreator.create(1);
        System.out.println(sn);

    }


}
