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
 * 店铺收藏更新收集
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-23 下午5:50
 */
public class DataCollectionSellerConsumerTest extends BaseTest {


    @Autowired
    private DataCollectionSellerConsumer dataCollectionSellerConsumer;
    @Autowired
    private ShopDataManager shopDataManager;

    @Autowired
    @Qualifier("sssDaoSupport")
    private DaoSupport daoSupport;

    @MockBean
    private ShopManager shopManager;

    @Before
    public void before() {
        this.daoSupport.execute("TRUNCATE TABLE es_sss_shop_data");
        this.daoSupport.execute("INSERT INTO `es_sss_shop_data` VALUES ('1', '9993', 'test1', '0', 'OPEN')");
        ShopVO shopVO = new ShopVO();
        shopVO.setShopId(9993L);
        shopVO.setShopName("test1");
        shopVO.setShopDisable(ShopStatusEnum.OPEN.value());
        when(shopManager.getShop(9993L)).thenReturn(shopVO);

    }


    @Test
    public void sellerCollectionChange() {
        ShopData shopData = new ShopData();
        shopData.setSellerId(9993L);
        shopData.setFavoriteNum(9998);
        shopDataManager.updateCollection(shopData);
        ShopData afterShop = shopDataManager.get(9993L);
        Assert.assertEquals(9998, afterShop.getFavoriteNum(), 0);

    }


}
