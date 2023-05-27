package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.model.shop.enums.ShopStatusEnum;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.service.shop.ShopManager;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import com.enation.app.javashop.service.statistics.ShopDataManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.when;

/**
 * 店铺数据收集
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-23 下午5:50
 */
public class DataShopConsumerTest extends BaseTest {

    @Autowired
    private DataShopConsumer dataShopConsumer;

    @Autowired
    @Qualifier("sssDaoSupport")
    private DaoSupport daoSupport;

    @MockBean
    private ShopManager shopManager;
    @Autowired
    private ShopDataManager shopDataManager;

    @Before
    public void before() {
        this.daoSupport.execute("TRUNCATE TABLE es_sss_shop_data");

        ShopVO shopVO = new ShopVO();
        shopVO.setShopId(9993l);
        shopVO.setShopName("test1");
        shopVO.setShopDisable(ShopStatusEnum.OPEN.value());
        when(shopManager.getShop(9993l)).thenReturn(shopVO);

    }

    @Test
    public void shopChange() {
        dataShopConsumer.shopChange(9993l);

        ShopData shopData = shopDataManager.get(9993l);
        ShopData expected = new ShopData();
        expected.setSellerId(9993L);
        expected.setFavoriteNum(0);
        expected.setSellerName("test1");
        expected.setShopDisable(ShopStatusEnum.OPEN.value());

        Assert.assertEquals(expected, shopData);

    }

}
