package com.enation.app.javashop.service.trade.order.plugin;

import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.service.trade.order.OrderOperateManager;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @description: 订单状态变更执行器
 * @author: liuyulei
 * @create: 2020-01-03 17:53
 * @version:1.0
 * @since:7.1.4
 **/
public abstract class OrderStatusChangeExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected final static  int MAX_TIMES_CHANGE = 3;
    @Autowired
    private OrderOperateManager orderOperateManager;


    @Autowired
    private MessageSender messageSender;



    /**
     * 修改订单状态
     *
     * @param sn          订单sn
     * @param times       次数
     * @param orderStatus 订单状态
     * @return 是否修改成功
     */
    public boolean updateTradeState(String sn, Integer times, OrderStatusEnum orderStatus) {
        try {
            // 失败三次后直接返回
            if (times >= MAX_TIMES_CHANGE) {
                logger.error("交易状态重试三次后更新失败,交易号为" + sn + ",重试");
                return false;
            }
            // 更改交易状态为已确认
            if (!orderOperateManager.updateTradeStatus(sn, orderStatus)) {
                // 如果更新失败，则等待1秒重试
                Thread.sleep(1000);
                return updateTradeState(sn, ++times, orderStatus);
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("交易状态更新失败,订单号为" + sn + ",重试" + ++times + "次,消息" + e.getMessage());
            updateTradeState(sn, ++times, orderStatus);
        }
        return false;
    }

    /**
     * 修改订单状态
     *
     * @param sn    订单sn
     * @param times 次数
     * @return 是否修改成功
     */
    public boolean updateState(String sn, Integer times, OrderStatusEnum status) {
        try {
            // 失败三次后直接返回
            if (times >= MAX_TIMES_CHANGE) {
                logger.error("订单状态重试三次后更新失败,订单号为" + sn + ",重试");
                return false;
            }
            // 更改订单状态为已确认
            boolean result = orderOperateManager.updateOrderStatus(sn, status);

            logger.debug("更新订单【" + sn + "】状态为[" + status + "]第[" + times + "次]结果：" + result);
            if (!result) {
                // 如果更新失败，则等待1秒重试
                Thread.sleep(1000);
                return updateState(sn, ++times, status);
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("订单状态更新失败,订单号为" + sn + ",重试" + ++times + "次,消息" + e.getMessage());
            updateState(sn, ++times, status);
        }
        return true;
    }


    /**
     * 发送MQ消息   修改订单状态
     * @param order
     */
    public void sendAmqpTemplate(OrderDO order) {
        boolean bool = this.updateState(order.getSn(), 0, OrderStatusEnum.PAID_OFF);
        if(bool){
            //此处说明订单状态更新成功，则发送订单状态变化消息
            OrderStatusChangeMsg orderStatusChangeMsg = new OrderStatusChangeMsg();
            orderStatusChangeMsg.setOldStatus(OrderStatusEnum.NEW);
            orderStatusChangeMsg.setNewStatus(OrderStatusEnum.PAID_OFF);
            order.setTradeSn(order.getTradeSn());
            order.setOrderStatus(OrderStatusEnum.PAID_OFF.name());
            orderStatusChangeMsg.setOrderDO(order);
            this.messageSender.send(new MqMessage(AmqpExchange.ORDER_STATUS_CHANGE,
                    AmqpExchange.ORDER_STATUS_CHANGE + "_ROUTING",
                    orderStatusChangeMsg));
        }
    }
}
