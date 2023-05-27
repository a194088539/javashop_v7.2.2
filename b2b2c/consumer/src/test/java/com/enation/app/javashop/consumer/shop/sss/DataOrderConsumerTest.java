package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.goods.dos.CategoryDO;
import com.enation.app.javashop.service.goods.CategoryManager;
import com.enation.app.javashop.model.statistics.dto.OrderData;
import com.enation.app.javashop.model.statistics.dto.OrderGoodsData;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 统计订单测试类
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018/5/2 上午11:29
 */
@Rollback(true)
public class DataOrderConsumerTest extends BaseTest {


    @Autowired
    private DataOrderConsumer dataOrderConsumer;

    @Autowired
    @Qualifier("sssDaoSupport")
    private DaoSupport daoSupport;

    @MockBean
    private OrderQueryManager orderQueryManager;
    @MockBean
    private CategoryManager categoryManager;

    @Before
    public void init() {

        this.daoSupport.execute("TRUNCATE TABLE es_sss_order_data");
        this.daoSupport.execute("TRUNCATE TABLE es_sss_order_goods_data");

        List<OrderItemsDO> list = new ArrayList<>();
        OrderItemsDO orderItemsDO = new OrderItemsDO();
        orderItemsDO.setCatId(3333L);
        orderItemsDO.setGoodsId(1332L);
        orderItemsDO.setName("orderitem name");
        orderItemsDO.setNum(3);
        orderItemsDO.setPrice(99.99);
        list.add(orderItemsDO);
        when(orderQueryManager.orderItems(anyString())).thenReturn(list);
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setCategoryPath("|0|1|2");
        when(categoryManager.getModel(anyLong())).thenReturn(categoryDO);
    }

    @Test
    public void testOrder() throws Exception{

        //准备工作
        OrderDO order = new OrderDO();

        order.setSn("18888888");
        order.setSellerId(132L);
        order.setSellerName("seller");
        order.setMemberName("buyer");
        order.setMemberId(123L);
        order.setOrderStatus("COD");
        order.setPayStatus("PAY_YES");
        order.setOrderPrice(99.66);
        order.setGoodsNum(1);
        order.setCreateTime(1421412412L);
        order.setShipProvinceId(33L);
        order.setShipCityId(244L);


        OrderStatusChangeMsg orderStatusChangeMsg = new OrderStatusChangeMsg();
        orderStatusChangeMsg.setOrderDO(order);
        orderStatusChangeMsg.setNewStatus(OrderStatusEnum.PAID_OFF);
        dataOrderConsumer.orderChange(orderStatusChangeMsg);
        OrderData orderData = daoSupport.queryForObject("select * from es_sss_order_data where sn = ?", OrderData.class, order.getSn());
        List<OrderGoodsData> orderGoodsData = daoSupport.queryForList("select * from es_sss_order_goods_data where order_sn = ?", OrderGoodsData.class, order.getSn());

        OrderData actual = new OrderData();
        actual.setId(1L);
        actual.setSn("18888888");
        actual.setBuyerId(123L);
        actual.setBuyerName("buyer");
        actual.setSellerId(132L);
        actual.setSellerName("seller");
        actual.setOrderStatus("COD");
        actual.setPayStatus("PAY_YES");
        actual.setOrderPrice(99.66);
        actual.setGoodsNum(3);
        actual.setShipProvinceId(33L);
        actual.setShipCityId(244L);
        actual.setCreateTime(1421412412L);
        Assert.assertEquals(actual.toString(),orderData.toString());

        OrderGoodsData actualGoodsData = new OrderGoodsData();
        actualGoodsData.setId(1L);
        actualGoodsData.setOrderSn("18888888");
        actualGoodsData.setGoodsId(1332L);
        actualGoodsData.setGoodsName("orderitem name");
        actualGoodsData.setGoodsNum(3);
        actualGoodsData.setPrice(99.99);
        actualGoodsData.setSubTotal(299.97);
        actualGoodsData.setCategoryPath("|0|1|2");
        actualGoodsData.setCategoryId(3333L);
        actualGoodsData.setCreateTime(1421412412L);
        actualGoodsData.setIndustryId(1L);

        Assert.assertEquals(actualGoodsData.toString(),orderGoodsData.get(0).toString());

        order.setPayStatus(PayStatusEnum.PAY_YES.value());
        order.setOrderStatus(OrderStatusEnum.PAID_OFF.value());
        orderStatusChangeMsg.setOrderDO(order);
        orderStatusChangeMsg.setNewStatus(OrderStatusEnum.COMPLETE);

        dataOrderConsumer.orderChange(orderStatusChangeMsg);
        orderData = daoSupport.queryForObject("select * from es_sss_order_data where sn = ?", OrderData.class, order.getSn());
        Assert.assertEquals(new OrderData(order).toString(),orderData.toString());
    }


}
