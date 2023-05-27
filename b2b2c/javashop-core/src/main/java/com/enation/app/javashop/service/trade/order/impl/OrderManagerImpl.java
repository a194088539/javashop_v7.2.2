package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.vo.*;
import com.enation.app.javashop.service.trade.order.OrderManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *  订单操作实现
 * @author Snow create in 2018/5/21
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class OrderManagerImpl implements OrderManager {

    @Autowired
    private OrderMapper orderMapper;


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public OrderDetailVO update(OrderDO orderDO) {

        orderMapper.updateById(orderDO);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(orderDO,orderDetailVO);

        return orderDetailVO;
    }


}
