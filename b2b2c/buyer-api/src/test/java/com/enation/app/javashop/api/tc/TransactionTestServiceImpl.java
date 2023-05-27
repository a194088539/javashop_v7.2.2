package com.enation.app.javashop.api.tc;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/6/13
 */
@Service
public class TransactionTestServiceImpl implements TransactionTestService {

    @Autowired
    SnCreator snCreator;

    @Autowired
    private DaoSupport daoSupport;

    Random rand = new Random();

    private int random() {
        int index = rand.nextInt(10);
        index++;
        return index;
    }

    @Autowired
    InnerTxServiceImpl innerTxService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    @ShardingTransactionType(TransactionType.XA)
    @Override
    public void orderAdd() {

      for (int i = 0; i < 100; i++) {

            Long sn = snCreator.create(1);
            String orderSn = "" + sn+random();

            OrderDO order = new OrderDO();
            order.setSellerId(123L);
            order.setSn(orderSn);
            order.setSellerName("seller");
            order.setMemberName("buyer");
            order.setMemberId(Long.valueOf(random()));
            order.setOrderStatus("COD");
            order.setPayStatus("PAY_YES");
            order.setOrderPrice(99.66);
            order.setGoodsNum(1);
            order.setCreateTime(1421412412L);
            order.setShipProvinceId(33L);
            order.setShipCityId(244L);
            daoSupport.insert(order);

            OrderMetaDO orderMetaDO = new OrderMetaDO();
            orderMetaDO.setMetaKey("testkey");
            orderMetaDO.setMetaValue("value");
            orderMetaDO.setOrderSn(orderSn);
            orderMetaDO.setStatus("ok");
            daoSupport.insert(orderMetaDO);

            innerTxService.addItem(orderSn,i);

//            if (i == 70) {
//                throw new RuntimeException("test");
//            }
     }
//        amqpTemplate.convertAndSend("tx",
//                "tx_ROUTING",
//                "test");

    }


}
