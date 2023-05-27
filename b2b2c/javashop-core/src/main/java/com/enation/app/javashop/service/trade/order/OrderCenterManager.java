package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.vo.OrderParam;

/**
 *
 * @description: 订单中心业务
 * @author: liuyulei
 * @create: 2020/3/23 18:39
 * @version:1.0
 * @since:
 **/
public interface OrderCenterManager {


    /**
     * 创建订单对象
     * @param orderParam  订单参数
     * @return 订单DTO
     */
    OrderDTO createOrder(OrderParam orderParam);




}
