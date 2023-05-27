package com.enation.app.javashop.service.trade.deposite.plugin;

import com.enation.app.javashop.service.trade.order.plugin.PaymentServicePlugin;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.deposite.RechargeDO;
import com.enation.app.javashop.service.trade.deposite.RechargeManager;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 预存款支付插件
 * @author: liuyulei
 * @create: 2019-12-30 16:51
 * @version:1.0
 * @since:7.1.4
 **/
@Service
public class DepositeServicePlugin implements PaymentServicePlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RechargeManager rechargeManager;

    @Override
    public String getServiceType() {
        return TradeTypeEnum.RECHARGE.name();
    }

    @Override
    public Double getPrice(String subSn) {
        return this.rechargeManager.getPrice(subSn);
    }

    @Override
    public boolean checkStatus(String subSn, Integer times) {
        try {
            //如果超过三次则直接返回false，不能支付
            if (times >= MAX_TIMES) {
                return false;
            }
            //订单或者交易状态
            String status = null;
            //获取订单详情，判断订单是否是已确认状态
            RechargeDO rechargeDO = rechargeManager.getModel(subSn);
            if (rechargeDO != null) {
                status = rechargeDO.getPayStatus();
            } else {
                throw new ServiceException(TradeErrorCode.E459.code(), "此充值订单不存在");
            }

            //检验订单状态是否是已确认可被支付
            if (!status.equals(PayStatusEnum.PAY_NO.value())) {
                Thread.sleep(1000);
                return this.checkStatus(subSn,  ++times);
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("检测充值订单是否可被支付,充值订单不可被支付，重试检测" + times + ",次，消息" + e.getMessage());
            this.checkStatus(subSn,  ++times);
        }
        return false;
    }

    @Override
    @Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void paySuccess(String subSn, String returnTradeNo, Double payPrice) {
        rechargeManager.paySuccess(subSn,payPrice);
    }

    @Override
    @Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void updatePaymentMethod(String subSn, String pluginId, String methodName) {
        this.rechargeManager.updatePaymentMethod(subSn,pluginId,methodName);
    }

    @Override
    public void balancePay(BalancePayVO payVO,Long memberId) {

    }
}
