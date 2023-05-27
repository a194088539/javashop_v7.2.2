package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.dos.TradeDO;

/**
 * 交易查询接口
 * @author Snow create in 2018/5/21
 * @version v2.0
 * @since v7.0.0
 */
public interface TradeQueryManager {

    /**
     * 根据交易单号查询交易对象
     * @param tradeSn
     * @return
     */
    TradeDO getModel(String tradeSn);


    /**
     * 检测交易/订单是否属于某会员
     * @param sn
     * @param memberId
     * @return true 是  false:不是
     */
    void checkIsOwner(String sn,Long memberId);

}
