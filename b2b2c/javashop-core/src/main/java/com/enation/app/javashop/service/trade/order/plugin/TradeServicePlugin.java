package com.enation.app.javashop.service.trade.order.plugin;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.TradeMapper;
import com.enation.app.javashop.model.errorcode.PaymentErrorCode;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.TradeDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;
import com.enation.app.javashop.service.trade.order.OrderOperateManager;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import com.enation.app.javashop.service.trade.order.TradeQueryManager;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 交易支付业务插件
 * @author: liuyulei
 * @create: 2019-12-27 14:16
 * @version:1.0
 * @since:7.1.4
 **/
@Service
public class TradeServicePlugin extends OrderStatusChangeExecutor implements PaymentServicePlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TradeQueryManager tradeQueryManager;

    @Autowired
    private TradeMapper tradeMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderQueryManager orderQueryManager;

    @Autowired
    private OrderOperateManager operateManager;

    @Autowired
    private DepositeClient depositeClient;



    @Override
    public String getServiceType() {
        return TradeTypeEnum.TRADE.name();
    }

    @Override
    public Double getPrice(String subSn) {

        TradeDO tradeDO = new QueryChainWrapper<>(tradeMapper)
                //查询总价格
                .select("total_price")
                //按交易编号查询
                .eq("trade_sn", subSn)
                //查询单个对象
                .one();

        if(tradeDO == null){
            return null;
        }

        return tradeDO.getTotalPrice();
    }

    @Override
    public boolean checkStatus(String subSn, Integer times) {
        try {
            //如果超过三次则直接返回false，不能支付
            if (times >= PaymentServicePlugin.MAX_TIMES) {
                return false;
            }
            //订单或者交易状态
            String status = null;

            //获取交易详情，判断交易是否是已确认状态
            TradeDO tradeDO = tradeQueryManager.getModel(subSn);
            if (tradeDO != null) {
                status = tradeDO.getTradeStatus();
            } else {
                throw new ServiceException(TradeErrorCode.E458.code(), "此交易不存在");
            }
            //检验交易是否是已确认可被支付
            if (!status.equals(OrderStatusEnum.CONFIRM.value())) {
                Thread.sleep(1000);
                return this.checkStatus(subSn,  ++times);
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("检测交易是否可被支付,订单不可被支付，重试检测" + times + ",次，消息" + e.getMessage());
            this.checkStatus(subSn,  ++times);
        }
        return false;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void paySuccess(String subSn, String returnTradeNo, Double payPrice) {
        //交易支付
        //修改订单交易号
        new UpdateChainWrapper<>(orderMapper)
                //设置支付方式返回的交易号
                .set("pay_order_no", returnTradeNo)
                //按交易编号修改
                .eq("trade_sn", subSn)
                //提交修改
                .update();

        //更新订单的支付状态
        List<OrderDetailDTO> orderList = orderQueryManager.getOrderByTradeSn(subSn);
        //判断交易的金额是否正确
        Double  totalPrice = 0d;
        for(OrderDetailDTO orderDetailDTO : orderList){
            totalPrice = CurrencyUtil.add(totalPrice,orderDetailDTO.getOrderPrice());
        }

        if(!totalPrice.equals(payPrice)){
            throw new ServiceException(PaymentErrorCode.E503.code(),"金额不一致");
        }

        for(OrderDetailDTO orderDetailDTO : orderList){
            operateManager.payOrder(orderDetailDTO.getSn(),orderDetailDTO.getNeedPayMoney(), returnTradeNo, OrderPermission.client);
        }
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updatePaymentMethod(String subSn, String pluginId, String methodName) {

        new UpdateChainWrapper<>(orderMapper)
                //设置支付插件id
                .set("payment_plugin_id", pluginId)
                //设置支付方式名称
                .set("payment_method_name", methodName)
                //按交易编号修改
                .eq("trade_sn", subSn)
                //提交修改
                .update();
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void balancePay(BalancePayVO payVO,Long memberId) {
        //查询交易
        TradeDO tradeDO = this.tradeQueryManager.getModel(payVO.getSn());
        //获取交易总价
        Double tradePrice = tradeDO.getTotalPrice();

        new UpdateChainWrapper<>(tradeMapper)
                //设置总价格
                .setSql("total_price = total_price - " + payVO.getBalance())
                //设置支付方式名称
                .set("payment_method_name", "预存款")
                //设置预存款抵扣金额
                .setSql("balance = balance + " + payVO.getBalance())
                //按交易编号修改
                .eq("trade_sn", payVO.getSn())
                //提交修改
                .update();

        List<OrderDetailDTO> list = this.orderQueryManager.getOrderByTradeSn(payVO.getSn());

        Double rate,balance = 0D;

        OrderDO order = new OrderDO();
        Long current = DateUtil.getDateline();
        //以每笔订单中的商品价格占交易中商品总价的比例计算每笔订单分配的预存款抵扣金额
        for (OrderDetailDTO detail: list) {

            //如果预存款支付金额满足交易金额，则不应该按比例尽行计算，否则会出现错误
            if(tradePrice.equals(payVO.getBalance())){

                balance = detail.getNeedPayMoney();
            }else{
                rate = CurrencyUtil.div(detail.getOrderPrice(),tradePrice,4);
                balance = CurrencyUtil.mul(payVO.getBalance(),rate);

            }

            new UpdateChainWrapper<>(orderMapper)
                    //设置应付金额
                    .setSql("need_pay_money = need_pay_money - " + balance)
                    //设置支付方式名称
                    .set("payment_method_name", "预存款")
                    //设置预存款抵扣金额
                    .setSql("balance = balance + " + balance)
                    //设置支付时间
                    .set("payment_time", current)
                    //按订单编号修改
                    .eq("sn", detail.getSn())
                    //提交修改
                    .update();

            this.depositeClient.reduce(balance,memberId,"商品订单支付，扣除预存款,订单号:" + detail.getSn());
            detail.setPaymentPluginId("balancePayPlugin");
            //更新订单状态
            if(CurrencyUtil.sub(detail.getNeedPayMoney(),balance) <= 0 ){
                BeanUtil.copyProperties(detail,order);
                order.setPaymentTime(current);
                order.setBalance(CurrencyUtil.add(order.getBalance(), payVO.getBalance()));
                sendAmqpTemplate(order);
            }
        }

        if(payVO.getNeedPay() == 0 ){
            super.updateTradeState(payVO.getSn(),0,OrderStatusEnum.PAID_OFF);
        }
    }

}
