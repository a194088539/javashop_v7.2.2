package com.enation.app.javashop.client.trade;

/**
 * 订单任务操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
public interface OrderTaskClient {

    /**
     * 款到发货，新订单未付款，自动变更：自动取消
     */
    void cancelTask();

    /**
     * 发货之后，自动变更：确认收货
     */
    void rogTask();

    /**
     * 确认收货后，自动变更：完成
     */
    void completeTask();

    /**
     * 货到付款订单，自动变更：已付款
     */
    void payTask();

    /**
     * 订单完成后，没有申请过售后，自动变更：售后超时
     */
    void serviceTask();

    /**
     * 订单完成后，多少天后，评论自动变更：好评。
     */
    void commentTask();

    /**
     * 自动交易投诉失效天数
     */
    void complainTask();


}
