package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.vo.TradeVO;

/**
 * 交易入库业务接口
 *
 * @author Snow create in 2018/5/9
 * @version v2.0
 * @since v7.0.0
 */
public interface TradeIntodbManager {

    /**
     * 入库处理
     * @param tradeVO
     */
    void intoDB(TradeVO tradeVO);


}
