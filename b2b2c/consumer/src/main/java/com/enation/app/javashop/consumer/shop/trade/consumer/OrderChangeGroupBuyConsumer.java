package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderOutStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderOutTypeEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 团购商品库存变更
 *
 * @author Snow create in 2018/5/22
 * @version v2.0
 * @since v7.0.0
 */
@Component
public class OrderChangeGroupBuyConsumer implements OrderStatusChangeEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private PromotionGoodsClient promotionGoodsClient;

    @Autowired
    private OrderOperateClient orderOperateClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        OrderDO orderDO = orderMessage.getOrderDO();
        //订单确认，优惠库存扣减
        if (orderMessage.getNewStatus().name().equals(OrderStatusEnum.CONFIRM.name())) {

            this.orderOperateClient.editOutStatus(orderDO.getSn(), OrderOutTypeEnum.GROUPBUY_GOODS, OrderOutStatusEnum.SUCCESS);

        } else if (orderMessage.getNewStatus().name().equals(OrderStatusEnum.CANCELLED.name())) {

            promotionGoodsClient.addGroupbuyQuantity(orderMessage.getOrderDO().getSn());
        } else if(orderMessage.getNewStatus().name().equals(OrderStatusEnum.INTODB_ERROR.name())){

            logger.error("团购库存扣减失败");
            this.orderOperateClient.editOutStatus(orderDO.getSn(), OrderOutTypeEnum.GROUPBUY_GOODS, OrderOutStatusEnum.FAIL);
        }

    }

}
