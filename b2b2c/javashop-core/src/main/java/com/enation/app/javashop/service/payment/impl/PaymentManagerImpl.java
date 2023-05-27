package com.enation.app.javashop.service.payment.impl;

import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.model.payment.vo.PaymentMethodVO;
import com.enation.app.javashop.service.payment.PaymentBillManager;
import com.enation.app.javashop.service.payment.PaymentManager;
import com.enation.app.javashop.service.payment.PaymentMethodManager;
import com.enation.app.javashop.service.payment.PaymentPluginManager;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付账单管理实现
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-04-10
 */
@Service
public class PaymentManagerImpl implements PaymentManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private PaymentBillManager paymentBillManager;

    @Autowired
    private List<PaymentPluginManager> paymentPluginList;

    @Autowired
    private Debugger debugger;

    @Autowired
    private PaymentMethodManager paymentMethodManager;



    @Override
    public Map pay(PayBill bill) {
        //调起相应的支付插件
        PaymentPluginManager paymentPlugin = this.findPlugin(bill.getPluginId());
        logger.debug("开始调起支付插件："+bill.getPluginId());
        debugger.log("开始调起支付插件："+bill.getPluginId());
        return paymentPlugin.pay(bill);
    }

    @Override
    public String payCallback(String paymentPluginId, ClientType clientType) {
        PaymentPluginManager plugin = this.findPlugin(paymentPluginId);
        if (plugin != null) {
            return plugin.onCallback(clientType);
        }
        return "fail";
    }

    @Override
    public List<PaymentMethodVO> queryPayments(String clientType) {
        List<PaymentMethodVO> list = paymentMethodManager.queryMethodByClient(clientType);
        return list;
    }

    @Override
    public void payReturn(TradeTypeEnum tradeType, String paymentPluginId) {
        PaymentPluginManager plugin = this.findPlugin(paymentPluginId);
        if (plugin != null) {
            plugin.onReturn(tradeType);
        }
    }

    @Override
    public String queryResult(String subSn,String serviceType) {

        //使用子订单号和业务类型查询账单bill
        PaymentBillDO paymentBill = paymentBillManager.getBySubSnAndServiceType(subSn, serviceType);

        //已经支付回调，则不需要查询
        if (paymentBill.getIsPay() == 1) {
            return "success";
        }

        PaymentPluginManager plugin = this.findPlugin(paymentBill.getPaymentPluginId());


        Map map = JsonUtil.jsonToObject(paymentBill.getPayConfig(), Map.class);
        List<Map> list = (List<Map>) map.get("config_list");

        Map<String, String> result = new HashMap<>(list.size());
        if (list != null) {
            for (Map item : list) {
                result.put(item.get("name").toString(), item.get("value").toString());
            }
        }

        return plugin.onQuery(paymentBill.getBillSn(),result);
    }

    @Override
    public Map pay(PayParam param) {

        //获取支付参数
        PaymentMethodDO paymentMethod = this.paymentMethodManager.getByPluginId(param.getPaymentPluginId());

        //组装bill信息
        PaymentBillDO tempBill = new PaymentBillDO(param.getSn(),param.getPrice(),param.getTradeType());
        tempBill.setPaymentPluginId(paymentMethod.getPluginId());
        tempBill.setPaymentName(paymentMethod.getMethodName());
        //保存支付参数
        ClientType clientType = ClientType.valueOf(param.getClientType());
        switch ( clientType )  {
            case PC:
                tempBill.setPayConfig(paymentMethod.getPcConfig());
                break;
            case WAP:
                tempBill.setPayConfig(paymentMethod.getWapConfig());
                break;
            case NATIVE:
                tempBill.setPayConfig(paymentMethod.getAppNativeConfig());
                break;
            case REACT:
                tempBill.setPayConfig(paymentMethod.getAppReactConfig());
                break;
            case MINI:
                tempBill.setPayConfig(paymentMethod.getMiniConfig());
                break;
            default:
                break;
        }
        //判断子订单是否已经创建过相关的账单，如果价格更改过，则作废原来的账单
        PayBill bill = paymentBillManager.add(tempBill);
        bill.setPayMode(param.getPayMode());
        bill.setClientType(ClientType.valueOf(param.getClientType()));

        Map map = this.pay(bill);
        map.put("payment_method",paymentMethod.getMethodName());

        return map;
    }


    /**
     * 查找支付插件
     *
     * @param pluginId
     * @return
     */
    private PaymentPluginManager findPlugin(String pluginId) {
        for (PaymentPluginManager plugin : paymentPluginList) {
            if (plugin.getPluginId().equals(pluginId)) {
                return plugin;
            }
        }
        return null;
    }




}
