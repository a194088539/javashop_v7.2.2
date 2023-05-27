package com.enation.app.javashop.consumer.job.execute.impl;

import com.enation.app.javashop.client.trade.OrderTaskClient;
import com.enation.app.javashop.consumer.job.execute.EveryDayExecute;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单状态扫描
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-05 下午2:11
 */
@Component
public class OrderStatusCheckJob implements EveryDayExecute {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderTaskClient orderTaskClient;

    /**
     * 每晚23:30执行
     */
    @Override
    public void everyDay() {
        /** 自动取消 */
        try {
            this.orderTaskClient.cancelTask();
        } catch (Exception e) {
            logger.error("订单自动取消出错", e);
        }

        /** 自动确认收货 */
        try {
            this.orderTaskClient.rogTask();
        } catch (Exception e) {
            logger.error("订单自动确认收货出错", e);
        }

        /** 自动完成天数 */
        try {
           this.orderTaskClient.completeTask();
        } catch (Exception e) {
            logger.error("订单自动标记为完成出错", e);
        }

        /** 自动支付天数 */
        try {
            this.orderTaskClient.payTask();
        } catch (Exception e) {
            logger.error("订单自动支付出错", e);
        }

        /** 售后失效天数 */
        try {
            this.orderTaskClient.serviceTask();
        } catch (Exception e) {
            logger.error("订单自动标记为售后过期出错", e);
        }

        /** 自动评价天数 */
        try {
            this.orderTaskClient.commentTask();
        } catch (Exception e) {
            logger.error("订单自动评价出错", e);
        }

        /** 自动交易投诉失效天数 */
        try {
            this.orderTaskClient.complainTask();
        } catch (Exception e) {
            logger.error("订单交易投诉失效出错", e);
        }

    }

}
