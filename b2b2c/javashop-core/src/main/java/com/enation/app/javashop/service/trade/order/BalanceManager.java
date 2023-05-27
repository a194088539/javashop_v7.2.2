package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;

/**
*
* @description: 预存款抵扣业务层
* @author: liuyulei
* @create: 2020/1/1 11:53
* @version:1.0
* @since:7.1.5
**/
public interface BalanceManager {


    /**
     * 预存款抵扣方饭
     * @param sn   交易编号/订单编号
     * @param tradeType 交易类型
     * @param password  交易密码
     */
    BalancePayVO balancePay(String sn,Long memberId, String tradeType, String password);

}
