package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;

/**
 * 订单操作接口
 * @author Snow create in 2018/5/21
 * @version v2.0
 * @since v7.0.0
 */
public interface OrderManager {

    /**
     * 修改订单信息
     * @param orderDO
     * @return
     */
    OrderDetailVO update(OrderDO orderDO);



}
