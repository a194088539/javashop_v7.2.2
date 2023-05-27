package com.enation.app.javashop.client.trade;

import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.vo.OrderParam;
import com.enation.app.javashop.model.trade.order.vo.TradeParam;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;

public interface OrderCenterClient {


    /**
     * 创建订单对象
     * @param orderParam
     * @return
     */
    OrderDTO createOrderDTO(OrderParam orderParam);


    /**
     * 创建交易对象
     * @param tradeParam
     * @return
     */
    TradeVO createTradeVO(TradeParam tradeParam);

    /**
     * 创建交易
     * @param tradeVO
     */
    void createTrade(TradeVO tradeVO);

}
