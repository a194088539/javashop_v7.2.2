package com.enation.app.javashop.consumer.shop.distribution;

import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.distribution.DistributionOrderClient;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 分销商订单处理
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/14 上午7:13
 */

@Component
public class DistributionOrderChangeConsumer implements OrderStatusChangeEvent{

    @Autowired
    private DistributionOrderClient distributionOrderClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());



    @Override
    public void orderChange(OrderStatusChangeMsg orderStatusChangeMsg) {
        OrderDO order = orderStatusChangeMsg.getOrderDO();
        try {
            if (orderStatusChangeMsg.getNewStatus().equals(OrderStatusEnum.ROG)) {
                distributionOrderClient.confirm(order);
            }
        } catch (Exception e) {
            logger.error("订单收款分销计算返利异常：",e);
        }
    }

}
