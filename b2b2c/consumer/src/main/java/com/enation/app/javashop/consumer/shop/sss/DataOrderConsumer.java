package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.consumer.core.event.ASNewOrderEvent;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.statistics.OrderDataClient;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单状态改变消费
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/8 下午6:44
 */
@Component
public class DataOrderConsumer implements OrderStatusChangeEvent, ASNewOrderEvent {

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private OrderDataClient orderDataClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderStatusChangeMsg) {
        try {
            if (orderStatusChangeMsg.getNewStatus().equals(OrderStatusEnum.PAID_OFF)) {
                this.orderDataClient.put(orderStatusChangeMsg.getOrderDO());
            } else {
                this.orderDataClient.change(orderStatusChangeMsg.getOrderDO());
            }
        } catch (Exception e) {
            logger.error("订单变更消息异常:",e);
            e.printStackTrace();
        }
    }

}
