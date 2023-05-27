package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.vo.TradeParam;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;

/**
 * 交易管理
 * @author liuyulei
 * @version v2.0
 * @since v7.1.5
 */
public interface TradeCenterManager {

    /**
     * 创建交易对象
     * @param tradeParam 交易参数
     * @return 交易VO
     */
    TradeVO createTrade(TradeParam tradeParam);

}
