package com.enation.app.javashop.consumer.shop.distribution;

import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.service.distribution.DistributionOrderManager;
import com.enation.app.javashop.service.statistics.util.DateUtil;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
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

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Rollback
public class DistributionOrderChangeConsumerTest extends BaseTest {


    @Autowired
    private DistributionOrderChangeConsumer distributionOrderChangeConsumer;
    @Autowired
    @Qualifier("distributionDaoSupport")
    private DaoSupport daoSupport;

    @Autowired
    private DistributionOrderManager distributionOrderManager;


    @MockBean
    private OrderQueryManager orderQueryManager;
    @MockBean
    private SettingClient settingClient;

    @Autowired
    private DistributionRefundConsumer distributionRefundConsumer;


    @Before
    public void beforeDistribution() {

        this.daoSupport.execute("Truncate es_distribution_goods");
        this.daoSupport.execute("insert into es_distribution_goods value(1,1998,50,100)");
        //DistributionBeforeTest.before(daoSupport);
        List<OrderItemsDO> orderItemsDOS = new ArrayList<OrderItemsDO>();
        OrderItemsDO orderItemsDO = new OrderItemsDO();
        orderItemsDO.setPrice(80000000D);
        orderItemsDO.setGoodsId(1998L);
        orderItemsDO.setNum(10);
        orderItemsDOS.add(orderItemsDO);

        when(orderQueryManager.orderItems("order_1998")).thenReturn(orderItemsDOS);

        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setGoodsPrice(9000000.00D);
        when(orderQueryManager.getModel(anyString(), anyObject())).thenReturn(orderDetailVO);


    }


    @Test
    public void orderChange() throws Exception {
        when(settingClient.get(SettingGroup.DISTRIBUTION)).thenReturn("{\"cycle\":30,\"goods_model\":0}");
        //传统订单
        order1();
        //退单
        returnOrder1();

        //商品返利订单
        when(settingClient.get(SettingGroup.DISTRIBUTION)).thenReturn("{\"cycle\":30,\"goods_model\":1}");
        order2();
        //退单
        returnOrder2();
    }

    private void order1() throws Exception {
        Long now = DateUtil.getDateline();
        OrderDO order = new OrderDO();
        order.setNeedPayMoney(10000000D);
        order.setShippingPrice(1000000D);
        order.setMemberId(2L);
        order.setCreateTime(now);
        order.setOrderId(1997L);
        order.setSn("order_1997");
        OrderStatusChangeMsg orderStatusChangeMsg = new OrderStatusChangeMsg();
        orderStatusChangeMsg.setNewStatus(OrderStatusEnum.ROG);
        orderStatusChangeMsg.setOrderDO(order);

        distributionOrderChangeConsumer.orderChange(orderStatusChangeMsg);

        DistributionOrderDO distributionOrderDO = new DistributionOrderDO();
        distributionOrderDO.setOrderId(1997l);
        distributionOrderDO.setOrderSn("order_1997");
        distributionOrderDO.setBuyerMemberName("test2");
        distributionOrderDO.setBuyerMemberId(2L);
        distributionOrderDO.setMemberIdLv1(1L);
        distributionOrderDO.setBillId(2L);
        distributionOrderDO.setOrderPrice(9000000.00D);
        distributionOrderDO.setCreateTime(now);
        distributionOrderDO.setGrade1Rebate(180000.00);
        distributionOrderDO.setGrade2Rebate(0D);
        distributionOrderDO.setSellerId(1L);
        distributionOrderDO.setSellerName("test");
        distributionOrderDO.setLv1Point(2D);
        DistributionOrderDO distributionOrderDO1 = distributionOrderManager.getModelByOrderSn(order.getSn());

        Assert.assertEquals(distributionOrderDO.toString(), distributionOrderDO1.toString());

    }

    private void returnOrder1() throws Exception {
        RefundDO refundDO = new RefundDO();
        refundDO.setRefundPrice(4500000D);
        refundDO.setOrderSn("order_1997");
//        RefundChangeMsg refundChangeMsg = new RefundChangeMsg(refundDO, RefundStatusEnum.APPLY);
//        distributionRefundConsumer.refund(refundChangeMsg);

        DistributionOrderDO actual = distributionOrderManager.getModelByOrderSn(refundDO.getOrderSn());

        DistributionOrderDO expected = new DistributionOrderDO();
        expected.setOrderId(1997L);
        expected.setOrderSn("order_1997");
        expected.setBuyerMemberName("test2");
        expected.setBuyerMemberId(2L);
        expected.setMemberIdLv1(1L);
        expected.setBillId(2L);
        expected.setOrderPrice(9000000.00D);
        expected.setGrade1Rebate(180000.00);
        expected.setGrade2Rebate(0D);
        expected.setSellerId(1L);
        expected.setSellerName("test");
        expected.setIsReturn(1);
        expected.setLv1Point(2D);
        expected.setReturnMoney(4500000D);
        expected.setGrade1SellbackPrice(90000D);
        Assert.assertEquals(expected.toString(), actual.toString());
    }




    private void order2() throws Exception {
        Long now = DateUtil.getDateline();
        OrderDO order = new OrderDO();
        order.setNeedPayMoney(10000000D);
        order.setShippingPrice(1000000D);
        order.setMemberId(2L);
        order.setCreateTime(now);
        order.setOrderId(1998L);
        order.setSn("order_1998");
        OrderStatusChangeMsg orderStatusChangeMsg = new OrderStatusChangeMsg();
        orderStatusChangeMsg.setNewStatus(OrderStatusEnum.ROG);
        orderStatusChangeMsg.setOrderDO(order);

        distributionOrderChangeConsumer.orderChange(orderStatusChangeMsg);

        DistributionOrderDO distributionOrderDO = new DistributionOrderDO();
        distributionOrderDO.setOrderId(1998L);
        distributionOrderDO.setOrderSn("order_1998");
        distributionOrderDO.setBuyerMemberName("test2");
        distributionOrderDO.setBuyerMemberId(2L);
        distributionOrderDO.setMemberIdLv1(1L);
        distributionOrderDO.setBillId(2L);
        distributionOrderDO.setOrderPrice(9000000.00D);
        distributionOrderDO.setCreateTime(now);
        distributionOrderDO.setGrade1Rebate(500D);
        distributionOrderDO.setGrade2Rebate(0D);
        distributionOrderDO.setSellerId(1L);
        distributionOrderDO.setSellerName("test");
        distributionOrderDO.setGoodsRebate("[{\"id\":1,\"goodsId\":1998,\"grade1Rebate\":50.0,\"grade2Rebate\":100.0}]");
        DistributionOrderDO actual = distributionOrderManager.getModelByOrderSn(order.getSn());

        Assert.assertEquals(distributionOrderDO.toString(), actual.toString());

    }


    private void returnOrder2() throws Exception {

        RefundDO refundDO = new RefundDO();
        refundDO.setRefundPrice(300000D);
        refundDO.setOrderSn("order_1998");
//        RefundChangeMsg refundChangeMsg = new RefundChangeMsg(refundDO, RefundStatusEnum.APPLY);
//        distributionRefundConsumer.refund(refundChangeMsg);

        DistributionOrderDO actual = distributionOrderManager.getModelByOrderSn(refundDO.getOrderSn());

        DistributionOrderDO expected = new DistributionOrderDO();
        expected.setOrderId(1998L);
        expected.setOrderSn("order_1998");
        expected.setBuyerMemberName("test2");
        expected.setBuyerMemberId(2L);
        expected.setMemberIdLv1(1L);
        expected.setBillId(2L);
        expected.setOrderPrice(9000000.00D);
        expected.setGrade1Rebate(500.00);
        expected.setGrade2Rebate(0D);
        expected.setSellerId(1L);
        expected.setSellerName("test");
        expected.setIsReturn(1);
        expected.setReturnMoney(300000D);
        expected.setGrade1SellbackPrice(500D);
        expected.setGoodsRebate("[{\"id\":1,\"goodsId\":1998,\"grade1Rebate\":50.0,\"grade2Rebate\":100.0}]");
        Assert.assertEquals(expected.toString(), actual.toString());
    }

}
