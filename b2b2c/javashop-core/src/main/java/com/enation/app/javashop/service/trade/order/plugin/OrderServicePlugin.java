package com.enation.app.javashop.service.trade.order.plugin;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import com.enation.app.javashop.service.trade.order.OrderOperateManager;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 订单支付业务插件
 * @author: liuyulei
 * @create: 2019-12-27 14:14
 * @version:1.0
 * @since:7.1.4
 **/
@Service
public class OrderServicePlugin extends OrderStatusChangeExecutor implements PaymentServicePlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderQueryManager orderQueryManager;

    @Autowired
    private OrderOperateManager operateManager;

    @Autowired
    private DepositeClient depositeClient;

    @Autowired
    private OrderMapper orderMapper;


    @Override
    public String getServiceType() {
        return TradeTypeEnum.ORDER.name();
    }


    @Override
    public Double getPrice(String subSn) {
        OrderDO orderDO = new QueryChainWrapper<>(orderMapper)
                //查询应付金额
                .select("need_pay_money")
                //按订单编号查询
                .eq("sn", subSn)
                //查询单个对象
                .one();

        if(orderDO == null){
            return null;
        }

        return orderDO.getNeedPayMoney();
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
            OrderDetailVO orderDetailVO = orderQueryManager.getModel(subSn,null);
            if (orderDetailVO != null) {
                status = orderDetailVO.getOrderStatus();
            } else {
                throw new ServiceException(TradeErrorCode.E459.code(), "此订单不存在");
            }

            //检验订单状态是否是已确认可被支付
            if (!status.equals(OrderStatusEnum.CONFIRM.value())) {
                Thread.sleep(1000);
                return this.checkStatus(subSn,  ++times);
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("检测订单是否可被支付,订单不可被支付，重试检测" + times + ",次，消息" + e.getMessage());
            this.checkStatus(subSn,  ++times);
        }
        return false;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void paySuccess(String subSn, String returnTradeNo, Double payPrice) {
        operateManager.payOrder(subSn,payPrice, returnTradeNo, OrderPermission.client);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePaymentMethod(String subSn, String pluginId, String methodName) {

        new UpdateChainWrapper<>(orderMapper)
                //设置支付插件id
                .set("payment_plugin_id", pluginId)
                //设置支付方式名称
                .set("payment_method_name", methodName)
                //按订单编号修改
                .eq("sn", subSn)
                //提交修改
                .update();

    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void balancePay(BalancePayVO payVO,Long memberId) {
        Long current = DateUtil.getDateline();

        new UpdateChainWrapper<>(orderMapper)
                //设置应付金额
                .setSql(" need_pay_money = need_pay_money - " + payVO.getBalance())
                //设置支付插件id为balancePayPlugin
                .set("payment_plugin_id", "balancePayPlugin")
                //设置支付方式为预存款
                .set("payment_method_name", "预存款")
                //设置预存款抵扣金额
                .setSql("balance = balance + " + payVO.getBalance())
                //设置支付时间
                .set("payment_time", current)
                //按订单编号修改
                .eq("sn", payVO.getSn())
                //提交修改
                .update();
        this.depositeClient.reduce(payVO.getBalance(),memberId,"商品订单支付，扣除预存款,订单号:" + payVO.getSn());

        OrderDO order = this.orderQueryManager.getOrder(payVO.getSn());
        order.setPaymentTime(current);
        order.setPaymentPluginId("balancePayPlugin");
        //如果待支付金额为0，则无需在线支付
        if(payVO.getNeedPay() == 0){
            order.setBalance(CurrencyUtil.add(order.getBalance(), payVO.getBalance()));
            super.sendAmqpTemplate(order);
        }
    }


}
