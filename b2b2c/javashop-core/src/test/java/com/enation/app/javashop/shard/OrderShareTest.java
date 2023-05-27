package com.enation.app.javashop.shard;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.test.BaseTest;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.trade.order.dos.*;
import com.enation.app.javashop.model.trade.order.enums.TradeStatusEnum;
import com.enation.app.javashop.service.trade.order.TradeQueryManager;
import com.vividsolutions.jts.util.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static com.enation.app.javashop.framework.util.StringUtil.random;

/**
 * 此次单元测试通过对的表插入、查询测试分片策略是否成功。
 * 直接运行可以验证插入、查询是否正常
 * 对于数据的分布情况需要打开 @Rollback(false) 注释，观察数据的分布情况是否正常
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/6/18
 */
//@Rollback(false)
public class OrderShareTest extends BaseTest {

    @Autowired
    private DaoSupport daoSupport;

    @Autowired
    SnCreator snCreator;

    @Autowired
    TradeQueryManager queryManager;


    @Test
    public void queryTest() {
        String sql = "select count(0) from es_trade";
        int count = daoSupport.queryForInt(sql);
        //order有3个
        Assert.equals(count, 3);

        sql = "select count(0) from es_order";
        count = daoSupport.queryForInt(sql);
        //order有9个
        Assert.equals(count, 9);

        sql = "select count(0) from es_order_log";
        count = daoSupport.queryForInt(sql);
        //order_log 有9个
        Assert.equals(count, 27);

        sql = "select count(0) from es_order_items";
        count = daoSupport.queryForInt(sql);
        //order_items 有9个
        Assert.equals(count, 27);

        sql = "select count(0) from es_order_meta";
        count = daoSupport.queryForInt(sql);
        //order_meta有27个
        Assert.equals(count, 27);
    }

    @Test
    public void addTrade() {
        for (int i = 0; i < 3; i++) {

            Long sn = snCreator.create(1);
            String tradeSn = "" + sn;
            TradeDO tradeDO = new TradeDO();
            tradeDO.setTradeSn(tradeSn);
            tradeDO.setGoodsPrice(100D);
            tradeDO.setMemberId(Long.valueOf(i));
            tradeDO.setTotalPrice(100D);
            tradeDO.setTradeStatus(TradeStatusEnum.NEW.name());
            tradeDO.setCreateTime(DateUtil.getDateline());
            daoSupport.insert(tradeDO);

            addOrder(tradeSn);
        }
    }


    public void addOrder(String tradeSn) {

        for (int i = 0; i < 3; i++) {

            Long sn = snCreator.create(1);
            String orderSn = "" + sn;

            OrderDO order = new OrderDO();
            order.setTradeSn(tradeSn);

            order.setSellerId(Long.valueOf(i));
            order.setSn(orderSn);
            order.setSellerName("seller");
            order.setMemberName("buyer");
            order.setMemberId(random());
            order.setOrderStatus("COD");
            order.setPayStatus("PAY_YES");
            order.setOrderPrice(99.66);
            order.setGoodsNum(1);
            order.setCreateTime(1421412412L);
            order.setShipProvinceId(33L);
            order.setShipCityId(244L);
            daoSupport.insert(order);

            addItem(orderSn);
            addLog(orderSn);
            addMeta(orderSn);

        }

    }


    /**
     * 添加order meta
     * @param orderSn
     */
    private void addMeta(String orderSn) {
        for (int i = 1; i < 4; i++) {
            OrderMetaDO orderMetaDO = new OrderMetaDO();
            orderMetaDO.setMetaKey("testkey");
            orderMetaDO.setMetaValue("value");
            orderMetaDO.setOrderSn(orderSn);
            orderMetaDO.setStatus("ok");
            daoSupport.insert(orderMetaDO);
        }
    }

    /**
     * 添加订单日志
     *
     * @param orderSn
     */
    private void addLog(String orderSn) {

        for (int i = 1; i < 4; i++) {
            OrderLogDO orderLogDO = new OrderLogDO();
            orderLogDO.setOrderSn(orderSn);
            orderLogDO.setMessage("测试");
            orderLogDO.setOpName("admin");
            daoSupport.insert(orderLogDO);
        }

    }

    /**
     * 添加订单项
     *
     * @param orderSn
     */
    private void addItem(String orderSn) {

        for (int i = 1; i < 4; i++) {
            OrderItemsDO orderItemsDO = new OrderItemsDO();
            orderItemsDO.setCatId(3333L);
            orderItemsDO.setGoodsId(Long.valueOf(i));
            orderItemsDO.setName("orderitem name " + i);
            orderItemsDO.setNum(3);
            orderItemsDO.setPrice(99.99);
            orderItemsDO.setOrderSn(orderSn);
            daoSupport.insert(orderItemsDO);

        }

    }

}
