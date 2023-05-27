package com.enation.app.javashop.service.trade.complain;

import com.enation.app.javashop.model.trade.complain.dos.OrderComplainCommunication;

import java.util.List;

/**
 * 交易投诉对话表业务层
 *
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-29 10:46:34
 */
public interface OrderComplainCommunicationManager {

    /**
     * 查询交易投诉对话表列表
     *
     * @param complainId   交易投诉id
     * @return WebPage
     */
    List<OrderComplainCommunication> list(long complainId);

    /**
     * 添加交易投诉对话表
     *
     * @param orderComplainCommunication 交易投诉对话表
     * @return OrderComplainCommunication 交易投诉对话表
     */
    OrderComplainCommunication add(OrderComplainCommunication orderComplainCommunication);

}
