package com.enation.app.javashop.service.payment.plugin.alipay;

import com.enation.app.javashop.model.payment.enums.AlipayConfigItem;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.payment.vo.ClientConfig;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.model.payment.vo.PayConfigItem;
import com.enation.app.javashop.model.payment.vo.RefundBill;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.service.payment.PaymentPluginManager;
import com.enation.app.javashop.service.payment.plugin.alipay.executor.AliPayPaymentAppExecutor;
import com.enation.app.javashop.service.payment.plugin.alipay.executor.AliPayPaymentExecutor;
import com.enation.app.javashop.service.payment.plugin.alipay.executor.AliPayPaymentWapExecutor;
import com.enation.app.javashop.service.payment.plugin.alipay.executor.AlipayRefundExcutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description: 支付宝支付插件
 * @date 2018/4/12 10:25
 * @since v7.0.0
 */
@Service
public class AlipayDirectPlugin implements PaymentPluginManager {

    @Autowired
    private AliPayPaymentAppExecutor aliPayPaymentAppExecutor;

    @Autowired
    private AliPayPaymentExecutor aliPayPaymentExecutor;

    @Autowired
    private AliPayPaymentWapExecutor aliPayPaymentWapExecutor;

    @Autowired
    private AlipayRefundExcutor alipayRefundExcutor;



    @Override
    public Map pay(PayBill bill) {

        //使用支付客户端判断调用哪个执行者
        if (bill.getClientType().equals(ClientType.PC)) {

            return aliPayPaymentExecutor.onPay(bill);
        }

        if (bill.getClientType().equals(ClientType.WAP)) {

            return aliPayPaymentWapExecutor.onPay(bill);
        }

        if (bill.getClientType().equals(ClientType.NATIVE) || bill.getClientType().equals(ClientType.REACT)) {

            return aliPayPaymentAppExecutor.onPay(bill);
        }
        return null;
    }

    @Override
    public String getPluginId() {
        return "alipayDirectPlugin";
    }

    @Override
    public String getPluginName() {
        return "支付宝";
    }

    @Override
    public List<ClientConfig> definitionClientConfig() {

        List<ClientConfig> resultList = new ArrayList<>();

        ClientConfig config = new ClientConfig();

        List<PayConfigItem> configList = new ArrayList<>();
        for (AlipayConfigItem value : AlipayConfigItem.values()) {
            PayConfigItem item = new PayConfigItem();
            item.setName(value.name());
            item.setText(value.getText());
            configList.add(item);
        }

        config.setKey(ClientType.PC.getDbColumn() + "&" + ClientType.WAP.getDbColumn() + "&" + ClientType.NATIVE.getDbColumn() + "&" + ClientType.REACT.getDbColumn());
        config.setConfigList(configList);
        config.setName("是否开启");

        resultList.add(config);

        return resultList;
    }


    @Override
    public void onReturn(TradeTypeEnum tradeType) {

        aliPayPaymentExecutor.onReturn(tradeType);
    }

    @Override
    public String onCallback( ClientType clientType) {

        return aliPayPaymentExecutor.onCallback( clientType);
    }

    @Override
    public String onQuery(String billSn,Map config) {

        return aliPayPaymentExecutor.onQuery(billSn,config);
    }

    @Override
    public boolean onTradeRefund(RefundBill bill) {

        return alipayRefundExcutor.refundPay(bill);
    }

    @Override
    public String queryRefundStatus(RefundBill bill) {

        return alipayRefundExcutor.queryRefundStatus(bill);
    }

    @Override
    public Integer getIsRetrace() {

        return 1;
    }
}
