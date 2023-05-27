package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.client.payment.PaymentClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.model.errorcode.PaymentErrorCode;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.service.trade.order.plugin.PaymentServicePlugin;
import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.service.trade.order.OrderPayManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description: 订单支付
 * @date 2018/4/1617:10
 * @description: 子业务发起支付  重构
 * @author: liuyulei
 * @create: 2019/12/27 18:15
 * @version:3.0
 * @since:7.1.5
 * @since v7.0.0
 */
@Service
public class OrderPayManagerImpl implements OrderPayManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<PaymentServicePlugin> paymentServicePlugin;

    @Autowired
    private PaymentClient paymentClient;

    @Autowired
    private OrderClient orderClient;

    @Override
    public Map pay(PayParam param) {

        PaymentServicePlugin servicePlugin = this.findServicePlugin(param.getTradeType());
        //对订单状态进行检测，只有已确认的订单才可以进行支付
        boolean isLegitimate = servicePlugin.checkStatus(param.getSn(), 0);

        //判断订单状态是否已确认可以订单支付，否抛出异常
        if (!isLegitimate) {
            logger.error("[" + param.getSn() + "]该订单状态不正确，无法支付");
            throw new ServiceException(PaymentErrorCode.E506.code(), "该交易状态不正确，无法支付");
        }
        //获取订单|交易单价格
        Double orderPrice = servicePlugin.getPrice(param.getSn());
        //设置价格
        param.setPrice(orderPrice);
        // 对一个子订单发起支付
        Map map = paymentClient.pay(param);
        //查询订单。
        OrderDO order = orderClient.getOrder(param.getSn());
        //交易方式名字
        String methodName = "";
        //订单编号不为空，且预存款金额大于0
        if (order != null && order.getBalance() > 0) {
            methodName = "预存款 + " + map.get("payment_method").toString();
        } else {
            methodName = map.get("payment_method").toString();
        }
        //更新订单或交易单支付方式
        servicePlugin.updatePaymentMethod(param.getSn(), param.getPaymentPluginId(), methodName);
        return map;
    }

    @Override
    public void paySuccess(String tradeType, String subSn, String returnTradeNo, Double payPrice) {
        PaymentServicePlugin device = findServicePlugin(tradeType);

        if (device == null) {
            logger.error("支付回调失败，原因为：【" + tradeType + "没有适配回调器】");
            throw new ServiceException(PaymentErrorCode.E507.code(), "支付回调失败，原因为：【" + tradeType + "没有适配回调器】");
        }

        //调用回调器完成交易状态的变更
        device.paySuccess(subSn, returnTradeNo, payPrice);
    }

    /**
     * 在支付子业务插件中  找到对应业务插件
     *
     * @param tradeType
     * @return
     */
    private PaymentServicePlugin findServicePlugin(String tradeType) {
        for (PaymentServicePlugin plugin : paymentServicePlugin) {
            if (tradeType.equals(plugin.getServiceType())) {
                return plugin;
            }
        }
        return null;
    }
}
