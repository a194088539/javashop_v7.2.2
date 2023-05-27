package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;

/**
 * 交易管理
 * @author Snow create in 2018/4/9
 * @version v2.0
 * @since v7.0.0
 */
public interface TradeManager {

    /**
     * 交易创建
     * @param clientType  客户的类型
     * @param way 检查获取方式
     * @return
     */
    TradeVO createTrade(String clientType, CheckedWay way);

}
