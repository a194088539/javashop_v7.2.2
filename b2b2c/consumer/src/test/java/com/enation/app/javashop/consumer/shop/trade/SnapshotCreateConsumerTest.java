package com.enation.app.javashop.consumer.shop.trade;

import com.enation.app.javashop.consumer.shop.trade.consumer.SnapshotCreateConsumer;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.vo.GoodsSnapshotVO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 * @version v1.0
 * @Description: 快照单元测试
 * @date 2018/8/2 9:54
 * @since v7.0.0
 */
@Transactional(value = "tradeTransactionManager", rollbackFor = Exception.class)
public class SnapshotCreateConsumerTest extends BaseTest {

    @Autowired
    private SnapshotCreateConsumer snapshotCreateConsumer;

    private OrderStatusChangeMsg changeMsg;

    @MockBean
    private GoodsClient goodsClient;

    @Before
    public void insertData() {

        changeMsg = new OrderStatusChangeMsg();
        OrderDO order = new OrderDO();
        List<OrderSkuVO> skuList = new ArrayList<>();
        OrderSkuVO sku = new OrderSkuVO();
        sku.setGoodsId(1L);
        skuList.add(sku);
        order.setItemsJson(JsonUtil.objectToJson(skuList));
        changeMsg.setOrderDO(order);

        //mock商品
        GoodsDO goods = new GoodsDO();
        goods.setGoodsId(1L);
        goods.setGoodsName("商品名称");

        BrandDO brandDO = new BrandDO();
        brandDO.setName("品牌名称");

        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setName("分类名称");

        GoodsSnapshotVO GoodsSnapshotVO = new GoodsSnapshotVO(goods,null,brandDO,categoryDO,null);

        Mockito.when(goodsClient.queryGoodsSnapShotInfo(1l)).thenReturn(GoodsSnapshotVO);
    }


    @Test
    public void testAddSnapshot() {

        this.snapshotCreateConsumer.orderChange(changeMsg);
    }


}
