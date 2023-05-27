package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.model.base.message.GoodsChangeMsg;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.service.goods.CategoryManager;
import com.enation.app.javashop.service.goods.GoodsQueryManager;
import com.enation.app.javashop.service.member.MemberCollectionGoodsManager;
import com.enation.app.javashop.model.statistics.dto.GoodsData;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * 统计商品数据
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018/3/23 上午12:03
 */
public class DataGoodsConsumerTest extends BaseTest {

    @Autowired
    @Qualifier("sssDaoSupport")
    private DaoSupport daoSupport;

    @Autowired
    private DataGoodsConsumer dataGoodsConsumer;

    @MockBean
    private GoodsQueryManager goodsQueryManager;

    @MockBean
    private MemberCollectionGoodsManager memberCollectionGoodsManager;

    @MockBean
    private CategoryManager categoryManager;

    @Before
    public void init() {

        this.daoSupport.execute("TRUNCATE TABLE es_sss_goods_data");
        //模拟数据
        GoodsDO goods = new GoodsDO();
        goods.setGoodsId(9527L);
        goods.setGoodsName("AMQP goods");
        goods.setBrandId(3L);
        goods.setCategoryId(123L);
        goods.setPrice(95.27);
        goods.setSellerId(123L);
        goods.setMarketEnable(1);

        Mockito.when(goodsQueryManager.getModel(Mockito.anyLong())).thenReturn(goods);
        Mockito.when(memberCollectionGoodsManager.getGoodsCollectCount(Mockito.anyLong())).thenReturn(9527);
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setCategoryPath("|0|1|2|");
        Mockito.when(categoryManager.getModel(Mockito.anyLong())).thenReturn(categoryDO);
    }

    /**
     * 构造修改后的商品
     */
    private void mockitoEditGoods() {
        GoodsDO goods = new GoodsDO();
        goods.setGoodsId(9527L);
        goods.setGoodsName("AMQP goods1");
        goods.setBrandId(31L);
        goods.setCategoryId(1231L);
        goods.setPrice(951.27);
        goods.setSellerId(123L);
        goods.setMarketEnable(1);
        Mockito.when(goodsQueryManager.getModel(Mockito.anyLong())).thenReturn(goods);

    }

    @Test
    public void testGoodsDataCollect() throws Exception {
        //新增商品
        GoodsChangeMsg goodsChangeMsg = new GoodsChangeMsg(new Long[]{9527L}, GoodsChangeMsg.ADD_OPERATION);
        dataGoodsConsumer.goodsChange(goodsChangeMsg);
        GoodsData actual = this.daoSupport.queryForObject("select * from es_sss_goods_data where goods_id = ?", GoodsData.class, 9527);
        GoodsData expected = new GoodsData();
        expected.setGoodsId(9527L);
        expected.setGoodsName("AMQP goods");
        expected.setBrandId(3L);
        expected.setCategoryId(123L);
        expected.setPrice(95.27);
        expected.setSellerId(123L);
        expected.setMarketEnable(1);
        expected.setFavoriteNum(9527);
        expected.setCategoryPath("|0|1|2|");

        Assert.assertEquals(expected.toString(), actual.toString());

        //修改商品
        mockitoEditGoods();
        goodsChangeMsg = new GoodsChangeMsg(new Long[]{9527L}, GoodsChangeMsg.UPDATE_OPERATION);
        dataGoodsConsumer.goodsChange(goodsChangeMsg);
        actual = this.daoSupport.queryForObject("select * from es_sss_goods_data where goods_id = ?", GoodsData.class, 9527);
        expected = new GoodsData();
        expected.setGoodsId(9527L);
        expected.setGoodsName("AMQP goods1");
        expected.setBrandId(31L);
        expected.setCategoryId(1231L);
        expected.setPrice(951.27);
        expected.setSellerId(123L);
        expected.setMarketEnable(1);
        expected.setFavoriteNum(9527);
        expected.setCategoryPath("|0|1|2|");

        Assert.assertEquals(expected.toString(), actual.toString());

        //删除商品
        goodsChangeMsg = new GoodsChangeMsg(new Long[]{9527L}, GoodsChangeMsg.DEL_OPERATION);
        dataGoodsConsumer.goodsChange(goodsChangeMsg);
        actual = this.daoSupport.queryForObject("select * from es_sss_goods_data where goods_id = ?", GoodsData.class, 9527);

        Assert.assertNull(actual);

    }


}
