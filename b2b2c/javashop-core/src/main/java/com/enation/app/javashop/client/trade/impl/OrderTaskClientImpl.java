package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.OrderTaskClient;
import com.enation.app.javashop.service.trade.order.OrderTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单任务操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
@Service
public class OrderTaskClientImpl implements OrderTaskClient {

    @Autowired
    private OrderTaskManager orderTaskManager;

    @Override
    public void cancelTask() {
        orderTaskManager.cancelTask();
    }

    @Override
    public void rogTask() {
        orderTaskManager.rogTask();
    }

    @Override
    public void completeTask() {
        orderTaskManager.completeTask();
    }

    @Override
    public void payTask() {
        orderTaskManager.payTask();
    }

    @Override
    public void serviceTask() {
        orderTaskManager.serviceTask();
    }

    @Override
    public void commentTask() {
        orderTaskManager.commentTask();
    }

    @Override
    public void complainTask() {
        orderTaskManager.complainTask();
    }
}
