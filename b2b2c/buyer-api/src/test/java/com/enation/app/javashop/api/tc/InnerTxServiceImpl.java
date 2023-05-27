package com.enation.app.javashop.api.tc;

import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.model.trade.order.dos.OrderItemsDO;
import com.enation.app.javashop.service.statistics.DashboardStatisticManager;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/6/13
 */

@Service
public class InnerTxServiceImpl {

    @Autowired
    DaoSupport daoSupport;

    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
//    @ShardingTransactionType
    public void addItem(String orderSn,int oIndex) {

        for (int i = 1; i < 4; i++) {
            OrderItemsDO orderItemsDO = new OrderItemsDO();
            orderItemsDO.setCatId(3333L);
            orderItemsDO.setGoodsId(Long.valueOf(i));
            orderItemsDO.setName("orderitem name " + i);
            orderItemsDO.setNum(3);
            orderItemsDO.setPrice(99.99);
            orderItemsDO.setOrderSn(orderSn);
            daoSupport.insert(orderItemsDO);

            if (oIndex == 70) {
                throw new RuntimeException("test");
            }

        }

    }

}
