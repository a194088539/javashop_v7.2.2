package com.enation.app.javashop.service.trade.order;

/**
*
* @description: 交易检测者
* @author: liuyulei
* @create: 2020/3/20 12:00
* @version:1.0
* @since: 7.2.0
**/
public interface TradeValidator {

    /**
     * 检测配送范围
     * @return
     */
    TradeValidator checkShipRange();

    /**
     * 检测商品合法性
     * @return
     */
    TradeValidator checkGoods();

    /**
     * 检测促销活动合法性
     * @return
     */
    TradeValidator checkPromotion();





}
