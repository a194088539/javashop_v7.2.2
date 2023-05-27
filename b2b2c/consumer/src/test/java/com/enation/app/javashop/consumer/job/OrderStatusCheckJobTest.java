package com.enation.app.javashop.consumer.job;

import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.consumer.core.receiver.OrderStatusChangeReceiver;
import com.enation.app.javashop.consumer.job.execute.impl.OrderStatusCheckJob;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.system.vo.SiteSetting;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import com.enation.app.javashop.model.trade.order.vo.OrderSettingVO;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.service.trade.order.OrderMetaManager;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

/**
 * 订单定时任务
 * @author Snow create in 2018/7/14
 * @version v2.0
 * @since v7.0.0
 */
@Transactional(value = "tradeTransactionManager",rollbackFor = Exception.class)
public class OrderStatusCheckJobTest extends BaseTest {

    @Autowired
    private OrderStatusCheckJob orderStatusCheckJob;


    @Autowired
    SnCreator snCreator;


    @Autowired
    private OrderQueryManager orderQueryManager;

    @MockBean
    private SettingClient settingClient;

    @MockBean
    private OrderStatusChangeReceiver changeReceiver;

    @MockBean
    private OrderMetaManager orderMetaManager;

    @Autowired
    @Qualifier("tradeDaoSupport")
    private DaoSupport daoSupport;

    @Autowired
    @Qualifier("goodsDaoSupport")
    private DaoSupport goodsDaoSupport;

    /** 未付款的订单，需自动取消 */
    private OrderDO orderDO;

    /** 已发货的订单，自动确认收货 */
    private OrderDO orderDO2;

    /** 确认收货的订单，自动完成 */
    private OrderDO orderDO3;

    /** 货到付款订单，自动变更：已付款 */
    private OrderDO orderDO4;

    /** 已完成的订单，自动标记售后过期 */
    private OrderDO orderDO5;

    /** 已完成的订单，自动好评 */
    private OrderDO orderDO6;

    private List<Long> goodsIds = new ArrayList<>();


    @Before
    public void testData() {

        OrderSettingVO orderSettingVO = new OrderSettingVO();
        orderSettingVO.setCancelOrderDay(1);
        orderSettingVO.setCommentOrderDay(1);
        orderSettingVO.setCompleteOrderDay(1);
        orderSettingVO.setCompleteOrderPay(1);
        orderSettingVO.setRogOrderDay(1);
        orderSettingVO.setServiceExpiredDay(1);
        when (this.settingClient.get(SettingGroup.TRADE)).thenReturn(JsonUtil.objectToJson(orderSettingVO));

        SiteSetting setting = new SiteSetting();
        setting.setTestMode(1);
        when (this.settingClient.get(SettingGroup.SITE)).thenReturn(JsonUtil.objectToJson(setting));

        GoodsDO goodsDO = new GoodsDO();
        goodsDO.setGoodsName("测试商品");
        goodsDO.setQuantity(100);
        goodsDO.setMarketEnable(100);
        goodsDO.setQuantity(100);
        this.goodsDaoSupport.insert(goodsDO);
        long  goodsId = this.goodsDaoSupport.getLastId("es_goods");
        goodsDO.setGoodsId(goodsId);
        goodsIds.add(goodsId);


        List<OrderSkuVO> orderSkuVOList = new ArrayList<>();
        OrderSkuVO skuVO = new OrderSkuVO();
        skuVO.setGoodsId(goodsId);
        skuVO.setName(goodsDO.getGoodsName());
        orderSkuVOList.add(skuVO);

        orderDO = new OrderDO();
        orderDO.setSn(""+snCreator.create(SubCode.ORDER));
        orderDO.setSellerId(10L);
        orderDO.setMemberId(1L);
        orderDO.setOrderStatus(OrderStatusEnum.CONFIRM.value());
        orderDO.setMemberName("buyer1");
        orderDO.setDisabled(0);
        orderDO.setPayStatus(PayStatusEnum.PAY_NO.value());
        orderDO.setShipStatus(ShipStatusEnum.SHIP_NO.value());
        orderDO.setCreateTime(DateUtil.getDateline());
        orderDO.setPaymentType(PaymentTypeEnum.ONLINE.value());
        orderDO.setCommentStatus(CommentStatusEnum.UNFINISHED.value());
        orderDO.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
        orderDO.setNeedPayMoney(100.0);
        orderDO.setOrderPrice(100.0);
        orderDO.setItemsJson(JsonUtil.objectToJson(orderSkuVOList));
        this.daoSupport.insert(orderDO);

        orderDO2 = new OrderDO();
        orderDO2.setSn(""+snCreator.create(SubCode.ORDER));
        orderDO2.setSellerId(10L);
        orderDO2.setMemberId(1L);
        orderDO2.setOrderStatus(OrderStatusEnum.SHIPPED.value());
        orderDO2.setMemberName("buyer1");
        orderDO2.setDisabled(0);
        orderDO2.setPayStatus(PayStatusEnum.PAY_YES.value());
        orderDO2.setShipStatus(ShipStatusEnum.SHIP_YES.value());
        orderDO2.setCreateTime(DateUtil.getDateline());
        orderDO2.setPaymentType(PaymentTypeEnum.ONLINE.value());
        orderDO2.setCommentStatus(CommentStatusEnum.UNFINISHED.value());
        orderDO2.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
        orderDO2.setNeedPayMoney(100.0);
        orderDO2.setOrderPrice(100.0);
        orderDO2.setShipTime(100l);
        orderDO2.setItemsJson(JsonUtil.objectToJson(orderSkuVOList));
        this.daoSupport.insert(orderDO2);

        List<FullDiscountGiftDO> giftList = new ArrayList();
        FullDiscountGiftDO giftDO = new FullDiscountGiftDO();
        giftDO.setGiftName("123123");
        giftDO.setGiftId(1l);
        giftDO.setSellerId(3L);
        giftDO.setGiftImg("path");
        giftList.add(giftDO);
        String giftJson2 = JsonUtil.objectToJson(giftList);
        when (orderMetaManager.getMetaValue(orderDO2.getSn(),OrderMetaKeyEnum.GIFT)).thenReturn(giftJson2);


        /** 已确认收货的订单，自动完成 */
        orderDO3 = new OrderDO();
        orderDO3.setSn(""+snCreator.create(SubCode.ORDER));
        orderDO3.setSellerId(10L);
        orderDO3.setMemberId(1L);
        orderDO3.setOrderStatus(OrderStatusEnum.ROG.value());
        orderDO3.setMemberName("buyer1");
        orderDO3.setDisabled(0);
        orderDO3.setPayStatus(PayStatusEnum.PAY_YES.value());
        orderDO3.setShipStatus(ShipStatusEnum.SHIP_ROG.value());
        orderDO3.setCreateTime(DateUtil.getDateline());
        orderDO3.setPaymentType(PaymentTypeEnum.ONLINE.value());
        orderDO3.setCommentStatus(CommentStatusEnum.UNFINISHED.value());
        orderDO3.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
        orderDO3.setNeedPayMoney(100.0);
        orderDO3.setOrderPrice(100.0);
        orderDO3.setSigningTime(100l);
        orderDO3.setItemsJson(JsonUtil.objectToJson(orderSkuVOList));
        this.daoSupport.insert(orderDO3);

        /** 货到付款,已收货的订单，自动变更：已付款 */
        orderDO4 = new OrderDO();
        orderDO4.setSn(""+snCreator.create(SubCode.ORDER));
        orderDO4.setSellerId(10L);
        orderDO4.setMemberId(1L);
        orderDO4.setOrderStatus(OrderStatusEnum.ROG.value());
        orderDO4.setMemberName("buyer1");
        orderDO4.setDisabled(0);
        orderDO4.setPayStatus(PayStatusEnum.PAY_NO.value());
        orderDO4.setShipStatus(ShipStatusEnum.SHIP_ROG.value());
        orderDO4.setCreateTime(DateUtil.getDateline());
        orderDO4.setPaymentType(PaymentTypeEnum.COD.value());
        orderDO4.setCommentStatus(CommentStatusEnum.UNFINISHED.value());
        orderDO4.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
        orderDO4.setNeedPayMoney(100.0);
        orderDO4.setOrderPrice(100.0);
        orderDO4.setSigningTime(100l);
        orderDO4.setItemsJson(JsonUtil.objectToJson(orderSkuVOList));
        this.daoSupport.insert(orderDO4);

        /** 已完成的订单，自动标记售后过期 */
        orderDO5 = new OrderDO();
        orderDO5.setSn(""+snCreator.create(SubCode.ORDER));
        orderDO5.setSellerId(10L);
        orderDO5.setMemberId(1L);
        orderDO5.setOrderStatus(OrderStatusEnum.COMPLETE.value());
        orderDO5.setMemberName("buyer1");
        orderDO5.setDisabled(0);
        orderDO5.setPayStatus(PayStatusEnum.PAY_YES.value());
        orderDO5.setShipStatus(ShipStatusEnum.SHIP_ROG.value());
        orderDO5.setCreateTime(DateUtil.getDateline());
        orderDO5.setPaymentType(PaymentTypeEnum.ONLINE.value());
        orderDO5.setCommentStatus(CommentStatusEnum.UNFINISHED.value());
        orderDO5.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
        orderDO5.setNeedPayMoney(100.0);
        orderDO5.setOrderPrice(100.0);
        orderDO5.setCompleteTime(100l);
        orderDO5.setItemsJson(JsonUtil.objectToJson(orderSkuVOList));
        this.daoSupport.insert(orderDO5);

        /** 已完成的订单，自动好评 */
        orderDO6 = new OrderDO();
        orderDO6.setSn(""+snCreator.create(SubCode.ORDER));
        orderDO6.setSellerId(10L);
        orderDO6.setMemberId(1L);
        orderDO6.setOrderStatus(OrderStatusEnum.COMPLETE.value());
        orderDO6.setMemberName("buyer1");
        orderDO6.setDisabled(0);
        orderDO6.setPayStatus(PayStatusEnum.PAY_YES.value());
        orderDO6.setShipStatus(ShipStatusEnum.SHIP_ROG.value());
        orderDO6.setCreateTime(DateUtil.getDateline());
        orderDO6.setPaymentType(PaymentTypeEnum.ONLINE.value());
        orderDO6.setCommentStatus(CommentStatusEnum.UNFINISHED.value());
        orderDO6.setServiceStatus(OrderServiceStatusEnum.NOT_APPLY.value());
        orderDO6.setNeedPayMoney(100.0);
        orderDO6.setOrderPrice(100.0);
        orderDO6.setItemsJson(JsonUtil.objectToJson(orderSkuVOList));
        orderDO6.setShipTime(1l);
        this.daoSupport.insert(orderDO6);

    }

    @Test
    public void testEveryDay() throws InterruptedException {

        Thread.sleep(2*1000);

        this.orderStatusCheckJob.everyDay();

        OrderDetailVO orderDetailVO = this.orderQueryManager.getModel(orderDO.getSn(),null);
        Assert.assertEquals(OrderStatusEnum.CANCELLED.name(),orderDetailVO.getOrderStatus());

        OrderDetailVO orderDetailVO2 = this.orderQueryManager.getModel(orderDO2.getSn(),null);
        Assert.assertEquals(OrderStatusEnum.ROG.name(),orderDetailVO2.getOrderStatus());

        OrderDetailVO orderDetailVO3 = this.orderQueryManager.getModel(orderDO3.getSn(),null);
        Assert.assertEquals(OrderStatusEnum.COMPLETE.name(),orderDetailVO3.getOrderStatus());

        OrderDetailVO orderDetailVO4 = this.orderQueryManager.getModel(orderDO4.getSn(),null);
        Assert.assertEquals(PayStatusEnum.PAY_YES.name(),orderDetailVO4.getPayStatus());

        OrderDetailVO orderDetailVO5 = this.orderQueryManager.getModel(orderDO5.getSn(),null);
        Assert.assertEquals(OrderServiceStatusEnum.EXPIRED.name(),orderDetailVO5.getServiceStatus());

        OrderDetailVO orderDetailVO6 = this.orderQueryManager.getModel(orderDO6.getSn(),null);
        Assert.assertEquals(CommentStatusEnum.FINISHED.name(),orderDetailVO6.getCommentStatus());

    }

    @After
    public void cleanDate() {
        for (Long goodsId : goodsIds) {
            this.goodsDaoSupport.execute("delete from es_goods where goods_id = ?", goodsId);
        }
    }

}
