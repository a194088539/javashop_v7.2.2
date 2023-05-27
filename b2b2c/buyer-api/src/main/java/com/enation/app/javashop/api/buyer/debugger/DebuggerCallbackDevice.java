package com.enation.app.javashop.api.buyer.debugger;

import com.enation.app.javashop.service.trade.order.plugin.PaymentServicePlugin;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 调试支付回调器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-04-16
 */

@Service
@ConditionalOnProperty(value = "javashop.debugger", havingValue = "true")
public class DebuggerCallbackDevice implements PaymentServicePlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String getServiceType() {
        return TradeTypeEnum.debugger.name();
    }

    @Override
    public Double getPrice(String subSn) {
        return null;
    }

    @Override
    public boolean checkStatus(String subSn, Integer times) {
        return false;
    }

    @Override
    public void paySuccess(String subSn, String returnTradeNo, Double payPrice) {
        logger.debug("支付回调：outTradeNo：【"+subSn+"】returnTradeNo：【"+returnTradeNo+"】payPrice：【"+payPrice+"】");
    }

    @Override
    public void updatePaymentMethod(String subSn, String pluginId, String methodName) {

    }

    @Override
    public void balancePay(BalancePayVO payVO,Long memberId) {

    }
}
