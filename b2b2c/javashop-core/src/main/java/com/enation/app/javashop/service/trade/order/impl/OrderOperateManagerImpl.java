package com.enation.app.javashop.service.trade.order.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.enation.app.javashop.mapper.trade.order.OrderItemsMapper;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.TradeMapper;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.member.MemberHistoryReceiptClient;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.support.FlowCheckOperate;
import com.enation.app.javashop.model.errorcode.TradeErrorCode;
import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.complain.enums.ComplainSkuStatusEnum;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderLogDO;
import com.enation.app.javashop.model.trade.order.dos.TradeDO;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.vo.*;
import com.enation.app.javashop.service.trade.order.*;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单流程操作
 *
 * @author Snow create in 2018/5/15
 * @version v2.0
 * @since v7.0.0
 */
@Service
public class OrderOperateManagerImpl implements OrderOperateManager {

    @Autowired
    private MemberHistoryReceiptClient memberHistoryReceiptClient;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TradeMapper tradeMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private OrderQueryManager orderQueryManager;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private OrderLogManager orderLogManager;

    @Autowired
    private OrderManager orderManager;

    @Autowired
    private TradeQueryManager tradeQueryManager;

    @Autowired
    private TradePriceManager tradePriceManager;

    @Autowired
    private OrderMetaManager orderMetaManager;


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void confirm(ConfirmVO confirmVO, OrderPermission permission) {
        executeOperate(confirmVO.getOrderSn(), permission, OrderOperateEnum.CONFIRM, confirmVO);
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public OrderDO payOrder(String orderSn, Double payPrice, String returnTradeNo, OrderPermission permission) {

        PayParam payParam = new PayParam();
        payParam.setPayPrice(payPrice);
        payParam.setReturnTradeNo(returnTradeNo);

        executeOperate(orderSn, permission, OrderOperateEnum.PAY, payParam);
        return null;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void ship(DeliveryVO deliveryVO, OrderPermission permission) {

        String orderSn = deliveryVO.getOrderSn();
        executeOperate(orderSn, permission, OrderOperateEnum.SHIP, deliveryVO);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void rog(RogVO rogVO, OrderPermission permission) {

        String orderSn = rogVO.getOrderSn();
        executeOperate(orderSn, permission, OrderOperateEnum.ROG, rogVO);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void cancel(CancelVO cancelVO, OrderPermission permission) {

        String orderSn = cancelVO.getOrderSn();
        executeOperate(orderSn, permission, OrderOperateEnum.CANCEL, cancelVO);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void complete(CompleteVO completeVO, OrderPermission permission) {
        String orderSn = completeVO.getOrderSn();
        executeOperate(orderSn, permission, OrderOperateEnum.COMPLETE, completeVO);
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateServiceStatus(String orderSn, OrderServiceStatusEnum serviceStatus) {

        new UpdateChainWrapper<>(orderMapper)
                //设置售后状态
                .set("service_status", serviceStatus.value())
                //按订单编号修改
                .eq("sn", orderSn)
                //提交修改
                .update();
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public OrderConsigneeVO updateOrderConsignee(OrderConsigneeVO orderConsignee) {

        OrderDetailVO orderDetailVO = this.orderQueryManager.getModel(orderConsignee.getOrderSn(), null);
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderDetailVO, orderDO);

        orderDO.setShipProvince(orderConsignee.getRegion().getProvince());
        orderDO.setShipProvinceId(orderConsignee.getRegion().getProvinceId());
        orderDO.setShipCity(orderConsignee.getRegion().getCity());
        orderDO.setShipCityId(orderConsignee.getRegion().getCityId());
        orderDO.setShipCounty(orderConsignee.getRegion().getCounty());
        orderDO.setShipCountyId(orderConsignee.getRegion().getCountyId());
        orderDO.setShipTown(orderConsignee.getRegion().getTown());
        orderDO.setShipTownId(orderConsignee.getRegion().getTownId());

        orderDO.setShipAddr(orderConsignee.getShipAddr());
        orderDO.setShipMobile(orderConsignee.getShipMobile());
        orderDO.setShipTel(orderConsignee.getShipTel());
        orderDO.setReceiveTime(orderConsignee.getReceiveTime());
        orderDO.setShipName(orderConsignee.getShipName());
        orderDO.setRemark(orderConsignee.getRemark());

        this.orderManager.update(orderDO);
        return orderConsignee;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateOrderPrice(String orderSn, Double orderPrice) {
        //修改的订单价格不能小于等于0
        if (orderPrice <= 0) {
            throw new ServiceException(TradeErrorCode.E471.code(), "订单金额必须大于0");
        }

        //获取订单详情信息
        OrderDetailVO orderDetailVO = this.orderQueryManager.getModel(orderSn, null);

        //订单权限判断
        this.checkPermission(OrderPermission.seller, orderDetailVO);

        //获取交易单信息
        TradeDO tradeDO = tradeQueryManager.getModel(orderDetailVO.getTradeSn());

        //获取原订单金额
        Double oldPrice = orderDetailVO.getOrderPrice();
        //计算出原订单金额和修改后订单金额的差额
        Double differencePrice = CurrencyUtil.sub(oldPrice, orderPrice);
        //交易总价=原交易价格-差额
        Double tradePrice = CurrencyUtil.sub(tradeDO.getTotalPrice(), differencePrice);
        //优惠总额=原优惠金额-差额
        Double discountPrice = CurrencyUtil.add(tradeDO.getDiscountPrice(), differencePrice);

        //如果优惠总额小于0，那么将优惠总额设置为0
        if (discountPrice < 0) {
            discountPrice = 0.0;
        }

        //修改交易价格
        this.tradePriceManager.updatePrice(tradeDO.getTradeSn(), tradePrice, discountPrice);

        //获取修改订单中商品
        List<OrderSkuVO> list = JsonUtil.jsonToList(orderDetailVO.getItemsJson(), OrderSkuVO.class);

        //获取订单元数据信息
        Double fullMinus = Double.valueOf(this.orderMetaManager.getMetaValue(orderSn, OrderMetaKeyEnum.FULL_MINUS));
        Double cashBack = Double.valueOf(this.orderMetaManager.getMetaValue(orderSn, OrderMetaKeyEnum.CASH_BACK));
        //优惠券金额
        String couponPriceStr = this.orderMetaManager.getMetaValue(orderSn, OrderMetaKeyEnum.COUPON_PRICE);
        Double couponPrice = 0D;
        if (!StringUtil.isEmpty(couponPriceStr)) {
            List<GoodsCouponPrice> couponList = JsonUtil.jsonToList(couponPriceStr, GoodsCouponPrice.class);
            for (GoodsCouponPrice goodsCouponPrice : couponList) {
                couponPrice += goodsCouponPrice.getCouponPrice();
            }
        }

        //订单商品原总价 = 订单实际支付价格 - 运费 + 返现金额 + 优惠券优惠金额
        Double goodsPrice = CurrencyUtil.add(CurrencyUtil.add(CurrencyUtil.sub(oldPrice, orderDetailVO.getShippingPrice()), cashBack), couponPrice);

        for (OrderSkuVO skuVO : list) {
            //商品原价
            Double originalPrice = skuVO.getOriginalPrice();
            //商品原价在订单商品总价中的占比（保留4位小数）
            Double ratio = CurrencyUtil.div(originalPrice, goodsPrice, 4);
            //商家修改订单价格后的商品价格 = 占比 * 订单价格
            Double nowPrice = CurrencyUtil.mul(ratio, orderPrice);
            //此商品实际支付总额
            Double actualTotol = CurrencyUtil.mul(nowPrice, skuVO.getNum());
            //重新初始化订单商品实际支付小计（退款时要用）
            skuVO.setActualPayTotal(actualTotol);
        }

        //订单修改价格后的优惠金额 = 订单原商品总额 - 修改后的订单总额
        Double orderDiscoutPrice = CurrencyUtil.sub(goodsPrice, orderPrice);
        //如果订单修改价格后的优惠金额小于优惠券优惠的金额，那么将订单修改价格后的优惠金额设置为优惠券优惠的金额，防止当修改后的订单总价高于原价时返现金额和商品总价为负数
        if (orderDiscoutPrice < couponPrice) {
            orderDiscoutPrice = couponPrice;
        }

        //修改订单价格
        new UpdateChainWrapper<>(orderMapper)
                //设置订单价格
                .set("order_price", orderPrice)
                //设置应付金额
                .set("need_pay_money", orderPrice)
                //设置优惠金额
                .set("discount_price", orderDiscoutPrice)
                //设置货物列表json
                .set("items_json", JsonUtil.objectToJson(list))
                //按订单编号修改
                .eq("sn", orderSn)
                //提交修改
                .update();

        //如果此订单已经开发票需要修改发票订单金额
        if (orderDetailVO.getNeedReceipt().equals(1)) {
            memberHistoryReceiptClient.updatePriceByOrderSn(orderPrice, orderSn);
        }

        //修改订单元数据信息，此处是为了退款时的金额计算正确所做的操作
        this.orderMetaManager.updateMetaValue(orderSn, OrderMetaKeyEnum.FULL_MINUS, CurrencyUtil.add(fullMinus, differencePrice).toString());
        this.orderMetaManager.updateMetaValue(orderSn, OrderMetaKeyEnum.CASH_BACK, CurrencyUtil.add(cashBack, differencePrice).toString());

        //记录操作日志
        OrderLogDO orderLogDO = new OrderLogDO();
        orderLogDO.setMessage("商家修改订单价格");
        orderLogDO.setOrderSn(orderSn);
        //目前此方法只有商家会调用，所以可以直接读当前登录的商家
        orderLogDO.setOpName(UserContext.getSeller().getSellerName());
        orderLogDO.setOpTime(DateUtil.getDateline());
        this.orderLogManager.add(orderLogDO);
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateCommentStatus(String orderSn, CommentStatusEnum commentStatus) {

        new UpdateChainWrapper<>(orderMapper)
                //设置评论是否完成
                .set("comment_status", commentStatus.name())
                //按订单标号修改
                .eq("sn", orderSn)
                //提交修改
                .update();
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateItemJson(String itemsJson, String orderSn) {

        new UpdateChainWrapper<>(orderMapper)
                .set("items_json", itemsJson)
                //按订单标号修改
                .eq("sn", orderSn)
                //提交修改
                .update();
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public boolean updateOrderStatus(String orderSn, OrderStatusEnum orderStatus) {

        new UpdateChainWrapper<>(orderMapper)
                //设置订单状态
                .set("order_status", orderStatus.value())
                //如果订单状态是已付款状态，设置付款状态为已付款
                .set(OrderStatusEnum.PAID_OFF.equals(orderStatus), "pay_status", PayStatusEnum.PAY_YES.value())
                //按订单编号修改
                .eq("sn", orderSn)
                //提交修改
                .update();

        return true;
    }


    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void executeOperate(String orderSn, OrderPermission permission, OrderOperateEnum orderOperate, Object paramVO) {
        final Long ONE = -1L;
        // 获取此订单
        OrderDetailVO orderDetailVO = orderQueryManager.getModel(orderSn, null);

        //1、验证操作者的权限
        this.checkPermission(permission, orderDetailVO);

        //2、验证此订单可进行的操作
        this.checkAllowable(permission, orderDetailVO, orderOperate);

        long nowTime = DateUtil.getDateline();

        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderDetailVO, orderDO);

        //要变更的订单状态
        OrderStatusEnum newStatus = null;

        //日志信息
        String logMessage = "操作信息";

        String operator = "系统默认";

        switch (orderOperate) {
            case CONFIRM:

                ConfirmVO confirmVO = (ConfirmVO) paramVO;
                logMessage = "确认订单";
                newStatus = OrderStatusEnum.CONFIRM;

                new UpdateChainWrapper<>(orderMapper)
                        //设置订单状态
                        .set("order_status", OrderStatusEnum.CONFIRM.value())
                        //按订单编号修改
                        .eq("sn", confirmVO.getOrderSn())
                        //提交修改
                        .update();
                orderDO.setOrderStatus(OrderStatusEnum.CONFIRM.name());
                break;

            case PAY:

                logMessage = "支付订单";
                newStatus = OrderStatusEnum.PAID_OFF;
                PayParam payParam = (PayParam) paramVO;
                Double payPrice = payParam.getPayPrice();
                String returnTradeNo = payParam.getReturnTradeNo();

                switch (permission) {
                    case buyer:
                        operator = orderDetailVO.getMemberName();
                        break;

                    case client:
                        operator = orderDetailVO.getMemberName();
                        break;

                    case seller:
                        operator = orderDetailVO.getSellerName();
                        break;

                    case admin:
                        operator = AdminUserContext.getAdmin().getUsername();
                        //管理员点击收款 ，默认是全款
                        String methodName = "管理员收款";
                        //如果预存款支付金额大于0，证明已经支付过预存款。
                        //在线支付的支付宝微信不会出现支付部分情况，所以不用考虑
                        if (orderDO.getBalance() > 0) {
                            methodName = "预存款 + 管理员收款";
                        }
//                        // 后台点击确认收款，清空支付方式
                        orderDO.setPaymentMethodId(ONE);
                        // 后台点击确认收款，清空支付方式
                        new UpdateChainWrapper<>(orderMapper)
                                //设置支付插件id为null
                                .set("payment_plugin_id", null)
                                //设置支付插件名称为methodName
                                .set("payment_method_name", methodName)
                                //按订单编号修改
                                .eq("sn", orderDO.getSn())
                                //提交修改
                                .update();
                        break;
                    default:
                        break;
                }

                //款到发货订单 卖家不能确认收款
                if (permission.equals(OrderPermission.seller) && orderDO.getPaymentType().equals(PaymentTypeEnum.ONLINE.name())) {
                    throw new NoPermissionException("无权操作此订单");
                }
                // 付款金额和订单金额不相等
                if (payPrice.compareTo(orderDO.getNeedPayMoney()) != 0) {
                    throw new ServiceException(TradeErrorCode.E454.code(), "付款金额和应付金额不一致");
                }

                //判断支付方式
                if (orderDO.getPaymentType().equals(PaymentTypeEnum.COD.value())) {

                    new UpdateChainWrapper<>(orderMapper)
                            //设置订单状态为已付款
                            .set("order_status", OrderStatusEnum.PAID_OFF.value())
                            //设置支付状态为已付款
                            .set("pay_status", PayStatusEnum.PAY_YES.value())
                            //设置发货状态为已发货
                            .set("ship_status", ShipStatusEnum.SHIP_ROG.value())
                            //设置已支付金额
                            .set("pay_money", payPrice)
                            //设置支付时间
                            .set("payment_time", nowTime)
                            //按订单编号修改
                            .eq("sn", orderDO.getSn())
                            //提交修改
                            .update();
                    orderDO.setShipStatus(ShipStatusEnum.SHIP_ROG.value());
                } else {

                    new UpdateChainWrapper<>(orderMapper)
                            //设置订单状态为已付款
                            .set("order_status", OrderStatusEnum.PAID_OFF.value())
                            //设置支付状态为已付款
                            .set("pay_status", PayStatusEnum.PAY_YES.value())
                            //设置已支付金额
                            .set("pay_money", payPrice)
                            //设置支付时间
                            .set("payment_time", nowTime)
                            //设置支付方式返回的交易号
                            .set("pay_order_no", returnTradeNo)
                            //按订单编号修改
                            .eq("sn", orderDO.getSn())
                            //提交修改
                            .update();
                }

                new UpdateChainWrapper<>(tradeMapper)
                        //设置交易状态
                        .set("trade_status", TradeStatusEnum.PAID_OFF.value())
                        //按交易编号修改
                        .eq("trade_sn", orderDO.getTradeSn())
                        //提交修改
                        .update();

                orderDO.setOrderStatus(OrderStatusEnum.PAID_OFF.name());
                orderDO.setPayStatus(PayStatusEnum.PAY_YES.value());
                orderDO.setPayMoney(orderDO.getNeedPayMoney());
                orderDO.setPaymentTime(nowTime);

                break;
            case SHIP:

                //检测订单是否已经申请售后
                if (OrderServiceStatusEnum.APPLY.name().equals(orderDO.getServiceStatus())
                        || OrderServiceStatusEnum.PASS.name().equals(orderDO.getServiceStatus())) {
                    throw new ServiceException(TradeErrorCode.E455.code(), "订单已申请退款，不能发货");
                }

                DeliveryVO deliveryVO = (DeliveryVO) paramVO;
                logMessage = "订单发货";
                newStatus = OrderStatusEnum.SHIPPED;
                operator = deliveryVO.getOperator();

                new UpdateChainWrapper<>(orderMapper)
                        //设置订单状态为已发货
                        .set("order_status", OrderStatusEnum.SHIPPED.value())
                        //设置发货状态为已发货
                        .set("ship_status", ShipStatusEnum.SHIP_YES.value())
                        //设置货运单号
                        .set("ship_no", deliveryVO.getDeliveryNo())
                        //设置送货时间
                        .set("ship_time", nowTime)
                        //设置物流公司
                        .set("logi_id", deliveryVO.getLogiId())
                        //设置物流公司名称
                        .set("logi_name", deliveryVO.getLogiName())
                        //按订单编号修改
                        .eq("sn", orderDetailVO.getSn())
                        //提交修改
                        .update();

                orderDO.setOrderStatus(OrderStatusEnum.SHIPPED.name());
                orderDO.setShipStatus(ShipStatusEnum.SHIP_YES.value());
                orderDO.setShipNo(deliveryVO.getDeliveryNo());
                orderDO.setShipTime(nowTime);
                orderDO.setLogiId(deliveryVO.getLogiId());
                orderDO.setLogiName(deliveryVO.getLogiName());

                break;
            case ROG:

                RogVO rogVO = (RogVO) paramVO;
                logMessage = "确认收货";
                newStatus = OrderStatusEnum.ROG;
                operator = rogVO.getOperator();

                //订单售后状态
                String orderServiceStatus = OrderServiceStatusEnum.EXPIRED.value();

                new UpdateChainWrapper<>(orderMapper)
                        //设置订单状态为已收货
                        .set("order_status", OrderStatusEnum.ROG.value())
                        //设置发货状态为已收货
                        .set("ship_status", ShipStatusEnum.SHIP_ROG.value())
                        //设置售后状态
                        .set("service_status", orderServiceStatus)
                        //设置签收时间
                        .set("signing_time", nowTime)
                        //按订单编号修改
                        .eq("sn", orderDO.getSn())
                        //提交修改
                        .update();

                orderDO.setOrderStatus(OrderStatusEnum.ROG.name());
                orderDO.setShipStatus(ShipStatusEnum.SHIP_ROG.value());
                orderDO.setServiceStatus(orderServiceStatus);
                orderDO.setSigningTime(nowTime);

                break;

            case CANCEL:

                CancelVO cancelVO = (CancelVO) paramVO;
                logMessage = "取消订单";
                newStatus = OrderStatusEnum.CANCELLED;
                operator = cancelVO.getOperator();

                new UpdateChainWrapper<>(orderMapper)
                        //设置订单状态为已取消
                        .set("order_status", OrderStatusEnum.CANCELLED.value())
                        //设置取消原因
                        .set("cancel_reason", cancelVO.getReason())
                        //按订单编号修改
                        .eq("sn", orderDO.getSn())
                        //提交修改
                        .update();

                orderDO.setOrderStatus(OrderStatusEnum.CANCELLED.name());
                orderDO.setCancelReason(cancelVO.getReason());
                break;

            case COMPLETE:

                CompleteVO completeVO = (CompleteVO) paramVO;
                logMessage = "订单已完成";
                newStatus = OrderStatusEnum.COMPLETE;
                operator = completeVO.getOperator();

                new UpdateChainWrapper<>(orderMapper)
                        //设置订单状态为已完成
                        .set("order_status", OrderStatusEnum.COMPLETE.value())
                        //设置完成时间
                        .set("complete_time", nowTime)
                        //按订单编号修改
                        .eq("sn", orderSn)
                        //提交修改
                        .update();

                orderDO.setOrderStatus(OrderStatusEnum.COMPLETE.name());
                orderDO.setCompleteTime(nowTime);

                break;

            default:
                break;
        }

        OrderStatusChangeMsg message = new OrderStatusChangeMsg();
        message.setOrderDO(orderDO);
        message.setOldStatus(OrderStatusEnum.valueOf(orderDO.getOrderStatus()));
        message.setNewStatus(newStatus);
        //发送订单状态变化消息
        this.messageSender.send(new MqMessage(AmqpExchange.ORDER_STATUS_CHANGE, "order-change-queue", message));

        // 记录日志
        OrderLogDO orderLogDO = new OrderLogDO();
        orderLogDO.setMessage(logMessage);
        orderLogDO.setOrderSn(orderSn);
        orderLogDO.setOpName(operator);
        orderLogDO.setOpTime(DateUtil.getDateline());
        this.orderLogManager.add(orderLogDO);

    }

    @Override
    public boolean updateTradeStatus(String sn, OrderStatusEnum orderStatus) {

        new UpdateChainWrapper<>(tradeMapper)
                //设置订单状态
                .set("trade_status", orderStatus.value())
                //按订单编号修改
                .eq("trade_sn", sn)
                //提交修改
                .update();

        return true;
    }

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateItemRefundPrice(OrderDetailVO order) {
        //获取订单的满减优惠总额
        double fullMinus = order.getFullMinus();
        //获取订单优惠券优惠的总额
        double couponPrice = order.getCouponPrice();
        //获取订单商品集合
        List<OrderSkuVO> skuVOList = order.getOrderSkuList();

        //订单参与满减促销活动商品的总额（还未减去满减优惠的金额总计）
        double fmTotal = 0.00;
        //订单所有商品的总额（还未减去满减优惠和优惠券优惠的金额总计）
        double allTotal = 0.00;
        //订单参与满减促销活动的商品集合
        List<OrderSkuVO> fmList = new ArrayList<>();
        //订单未参与满减促销活动的商品集合
        List<OrderSkuVO> noFmList = new ArrayList<>();

        //循环获取订单中参与满减活动的商品集合、为参与满减活动的商品集合、参与满减活动商品金额总计和订单商品全部金额总计
        for (OrderSkuVO orderSkuVO : skuVOList) {
            //如果orderSkuVo中组合活动集合不为空并且长度不为0，那么则证明当前商品参与了满减活动
            if (orderSkuVO.getGroupList() != null && orderSkuVO.getGroupList().size() != 0) {
                fmTotal = CurrencyUtil.add(fmTotal, orderSkuVO.getSubtotal());
                fmList.add(orderSkuVO);
            } else {
                noFmList.add(orderSkuVO);
            }

            allTotal = CurrencyUtil.add(allTotal, orderSkuVO.getSubtotal());
        }

        //获取订单计算退款金额方式
        int countType = this.getCountType(fmList, noFmList, couponPrice);

        /**
         * 计算订单项退款金额
         *
         * countType说明:
         * 1：订单所有商品都没有参与满减优惠活动也没有使用优惠券
         * 2：订单所有商品都没有参与满减活动，但是订单使用了优惠券
         * 3：订单商品全部都参与了满减活动，但是没有使用优惠券
         * 4：订单商品一部分参与了满减活动，一部分没有参与满减活动，并且没有使用优惠券
         * 5：订单商品全部都参与了满减活动并且使用了优惠券
         * 6：订单商品一部分参与了满减活动，一部分没有参与满减活动，并且使用了优惠券
         */
        switch (countType) {
            case 1:
                updateItemRefundPrice(order.getSn(), skuVOList);
                break;

            case 2:
                //获取未参与满减活动商品总数-1的数值（为了兼容金额比例无法整除而导致多个商品的退款金额总和与退款金额不一致的问题）
                int noFmNum = noFmList.size() - 1;

                //剩余的订单优惠券优惠总额
                double surplusCouponPrice = couponPrice;

                updateCouponItemRefundPrice(order.getSn(), noFmList, couponPrice, allTotal, noFmNum, surplusCouponPrice);
                break;

            case 3:
                updateFmItemsRefundPrice(order.getSn(), fmList, fullMinus, fmTotal);
                break;

            case 4:
                updateFmItemsRefundPrice(order.getSn(), fmList, fullMinus, fmTotal);

                updateItemRefundPrice(order.getSn(), noFmList);
                break;

            case 5:
                updateFmItemsRefundPrice(order.getSn(), fmList, CurrencyUtil.add(fullMinus, couponPrice), fmTotal);
                break;

            case 6:
                updateFmCouponItemsRefundPrice(order.getSn(), fmList, noFmList, couponPrice, allTotal, fullMinus, fmTotal);
                break;

            default:
                break;
        }
    }

    @Override
    public void editOrderShopName(Long shopId, String shopName) {

        new UpdateChainWrapper<>(orderMapper)
                //设置商家名称
                .set("seller_name", shopName)
                //按商家id修改
                .eq("seller_id", shopId)
                //提交修改
                .update();
    }

    @Override
    public void updateItemsCommentStatus(String orderSn, Long goodsId, CommentStatusEnum commentStatus) {

        new UpdateChainWrapper<>(orderItemsMapper)
                //设置评论状态
                .set("comment_status", commentStatus.name())
                //拼接订单编号修改条件
                .eq("order_sn", orderSn)
                //拼接商品id修改条件
                .eq("goods_id", goodsId)
                //提交修改
                .update();

        checkOrderCommentStatus(orderSn, commentStatus);
    }

    @Override
    public void updateOrderItemsComplainStatus(String orderSn, Long skuId, Long complainId, ComplainSkuStatusEnum status) {

        OrderDetailVO orderDetailVO = this.orderQueryManager.getModel(orderSn, null);
        List<OrderSkuVO> orderSkuVOList = orderDetailVO.getOrderSkuList();
        for (OrderSkuVO orderSkuVO : orderSkuVOList) {
            if (orderSkuVO.getSkuId().equals(skuId)) {
                orderSkuVO.setComplainStatus(status.name());
                orderSkuVO.setComplainId(complainId);
            }
        }
        this.updateItemJson(JsonUtil.objectToJson(orderSkuVOList), orderSn);

    }


    private void checkOrderCommentStatus(String orderSn, CommentStatusEnum commentStatusEnum) {

        String commentStatus = "";
        //如果要修改为待追评状态，则需要检测订单项中是否包含未评论的
        if (CommentStatusEnum.WAIT_CHASE.equals(commentStatusEnum)) {
            commentStatus = CommentStatusEnum.UNFINISHED.name();
        }

        //如果要修改为评论完成状态，则需要检测订单项中是否包含待追评的信息
        if (CommentStatusEnum.FINISHED.equals(commentStatusEnum)) {
            commentStatus = CommentStatusEnum.WAIT_CHASE.name();
        }

        Integer count = new QueryChainWrapper<>(orderItemsMapper)
                //拼接订单编号查询条件
                .eq("order_sn", orderSn)
                //拼接评论状态查询条件
                .eq("comment_status", commentStatus)
                //查询数量
                .count();
        //如果不存在了，则修改订单的评论状态，否则不修改
        if (count == 0) {
            this.updateCommentStatus(orderSn, commentStatusEnum);
        }
    }

    /**
     * 修改订单项的退款金额
     * 调用说明：1.针对订单全部商品都没有参与满减活动也没有使用优惠券的情况
     * 2.针对订单部分商品都没有参与满减活动也没有使用优惠券的情况
     *
     * @param orderSn   订单编号
     * @param skuVOList 订单商品数据
     */
    private void updateItemRefundPrice(String orderSn, List<OrderSkuVO> skuVOList) {
        for (OrderSkuVO orderSkuVO : skuVOList) {
            double refundPrice = orderSkuVO.getActualPayTotal();
            updateRefundPrice(orderSn, orderSkuVO, refundPrice);
        }
    }

    /**
     * 修改订单项的退款金额
     * 调用说明：1.针对订单全部商品都参与了满减促销活动并且没有使用优惠券的情况
     * 2.针对订单全部商品都参与了满减促销活动并且使用了优惠券的情况
     * 3.针对订单一部分商品参与了满减促销活动并且没有使用优惠券的情况
     *
     * @param orderSn   订单编号
     * @param fmList    参与满减促销活动的订单商品集合
     * @param fullMinus 满减的总金额
     * @param fmTotal   参与满减促销活动的商品金额总计（还未减去满减优惠的金额总计）
     */
    private void updateFmItemsRefundPrice(String orderSn, List<OrderSkuVO> fmList, double fullMinus, double fmTotal) {
        //获取参与满减活动商品总数-1的数值（为了兼容金额比例无法整除而导致多个商品的退款金额总和与退款金额不一致的问题）
        int num = fmList.size() - 1;
        //剩余的订单满减总额
        double surplusFmPrice = fullMinus;

        for (int i = 0; i < fmList.size(); i++) {
            OrderSkuVO orderSkuVO = fmList.get(i);
            //当前商品的应退款金额
            double refundPrice = 0.00;

            if (i != num) {
                //获取当前商品满减的占比金额
                double fmRatioPrice = CurrencyUtil.mul(CurrencyUtil.div(orderSkuVO.getSubtotal(), fmTotal, 4), fullMinus);
                //当前商品应退款金额=金额总计-满减的占比金额
                refundPrice = CurrencyUtil.sub(orderSkuVO.getSubtotal(), fmRatioPrice);
                //计算剩余的满减总额
                surplusFmPrice = CurrencyUtil.sub(surplusFmPrice, fmRatioPrice);
            } else {
                //当前商品应退款金额=金额总计-剩余的满减总额
                refundPrice = CurrencyUtil.sub(orderSkuVO.getSubtotal(), surplusFmPrice);
            }

            updateRefundPrice(orderSn, orderSkuVO, refundPrice);
        }
    }

    /**
     * 修改订单项的退款金额
     * 调用说明：针对订单一部分商品参与了满减促销活动，一部分商品没有参与满减促销活动并且还使用了优惠券的情况
     *
     * @param orderSn     订单编号
     * @param fmList      参与满减促销活动的订单商品集合
     * @param noFmList    未参与满减促销活动的订单商品集合
     * @param couponPrice 订单使用的优惠券优惠金额
     * @param allTotal    所有商品的金额总计（还未减去满减优惠和优惠券优惠的金额总计）
     * @param fullMinus   满减的总金额
     * @param fmTotal     参与满减促销活动的商品金额总计（还未减去满减优惠的金额总计）
     */
    private void updateFmCouponItemsRefundPrice(String orderSn, List<OrderSkuVO> fmList, List<OrderSkuVO> noFmList, double couponPrice, double allTotal, double fullMinus, double fmTotal) {
        //获取参与满减活动商品总数-1的数值（为了兼容金额比例无法整除而导致多个商品的退款金额总和与退款金额不一致的问题）
        int fmMum = fmList.size() - 1;
        //获取未参与满减活动商品总数-1的数值（为了兼容金额比例无法整除而导致多个商品的退款金额总和与退款金额不一致的问题）
        int noFmNum = noFmList.size() - 1;

        //剩余的订单满减总额
        double surplusFmPrice = fullMinus;
        //剩余的订单优惠券优惠总额
        double surplusCouponPrice = couponPrice;

        for (int i = 0; i < fmList.size(); i++) {
            OrderSkuVO orderSkuVO = fmList.get(i);
            double refundPrice = 0.00;

            if (i != fmMum) {
                //获取当前商品满减的占比金额
                double fmRatioPrice = CurrencyUtil.mul(CurrencyUtil.div(orderSkuVO.getSubtotal(), fmTotal, 4), fullMinus);
                //获取当前商品优惠券优惠的占比金额
                double couponRatioPrice = CurrencyUtil.mul(CurrencyUtil.div(orderSkuVO.getSubtotal(), allTotal, 4), couponPrice);
                //当前商品应退款金额=金额总计-(满减的占比金额+优惠券优惠的占比金额)
                refundPrice = CurrencyUtil.sub(orderSkuVO.getSubtotal(), CurrencyUtil.add(fmRatioPrice, couponRatioPrice));
                //计算剩余的满减总额
                surplusFmPrice = CurrencyUtil.sub(surplusFmPrice, fmRatioPrice);
                //计算剩余的优惠券优惠总额
                surplusCouponPrice = CurrencyUtil.sub(surplusCouponPrice, couponRatioPrice);
            } else {
                //获取当前商品优惠券优惠的占比金额
                double couponRatioPrice = CurrencyUtil.mul(CurrencyUtil.div(orderSkuVO.getSubtotal(), allTotal, 4), couponPrice);
                //当前商品应退款金额=金额总计-(优惠券优惠的占比金额+剩余的满减总额)
                refundPrice = CurrencyUtil.sub(orderSkuVO.getSubtotal(), CurrencyUtil.add(couponRatioPrice, surplusFmPrice));
                //计算剩余的优惠券优惠总额
                surplusCouponPrice = CurrencyUtil.sub(surplusCouponPrice, couponRatioPrice);
            }

            updateRefundPrice(orderSn, orderSkuVO, refundPrice);
        }

        updateCouponItemRefundPrice(orderSn, noFmList, couponPrice, allTotal, noFmNum, surplusCouponPrice);
    }


    /**
     * 修改订单项的退款金额
     * 调用说明：1.针对订单一部分商品参与了满减促销活动，一部分商品没有参与满减促销活动并且还使用了优惠券的情况
     * 2.针对订单商品全部没有参与满减活动并且使用了优惠券的情况
     *
     * @param orderSn            订单编号
     * @param noFmList           没有参与满减促销活动的订单商品集合
     * @param couponPrice        订单使用的优惠券优惠金额
     * @param allTotal           所有商品的金额总计（还未减去满减优惠和优惠券优惠的金额总计）
     * @param noFmNum            获取未参与满减活动商品总数-1的数值（为了兼容金额比例无法整除而导致多个商品的退款金额总和与退款金额不一致的问题）
     * @param surplusCouponPrice 剩余的订单优惠券优惠总额
     */
    private void updateCouponItemRefundPrice(String orderSn, List<OrderSkuVO> noFmList, double couponPrice, double allTotal, int noFmNum, double surplusCouponPrice) {
        for (int i = 0; i < noFmList.size(); i++) {
            OrderSkuVO orderSkuVO = noFmList.get(i);
            double refundPrice = 0.00;

            if (i != noFmNum) {
                //获取当前商品优惠券优惠的占比金额
                double couponRatioPrice = CurrencyUtil.mul(CurrencyUtil.div(orderSkuVO.getSubtotal(), allTotal, 4), couponPrice);
                //当前商品应退款金额=金额总计-优惠券优惠的占比金额
                refundPrice = CurrencyUtil.sub(orderSkuVO.getSubtotal(), couponRatioPrice);
                //计算剩余的优惠券优惠总额
                surplusCouponPrice = CurrencyUtil.sub(surplusCouponPrice, couponRatioPrice);
            } else {
                //当前商品应退款金额=金额总计-计算剩余的优惠券优惠总额
                refundPrice = CurrencyUtil.sub(orderSkuVO.getSubtotal(), surplusCouponPrice);
            }

            //如果计算出的可退款金额为负数，则将可退款金额置为0
            if (refundPrice < 0) {
                refundPrice = 0.00;
            }

            updateRefundPrice(orderSn, orderSkuVO, refundPrice);
        }
    }

    /**
     * 修改订单项可退款金额的公共方法
     *
     * @param orderSn     订单编号
     * @param orderSkuVO  订单商品明细
     * @param refundPrice 可退款金额
     */
    private void updateRefundPrice(String orderSn, OrderSkuVO orderSkuVO, double refundPrice) {

        new UpdateChainWrapper<>(orderItemsMapper)
                //设置可退款金额
                .set("refund_price", refundPrice)
                //拼接订单编号修改条件
                .eq("order_sn", orderSn)
                //拼接商品id修改条件
                .eq("goods_id", orderSkuVO.getGoodsId())
                //拼接货品id修改条件
                .eq("product_id", orderSkuVO.getSkuId())
                //提交修改
                .update();
    }

    /**
     * 获取计算方式
     *
     * @param activityList   订单参与满减活动的商品集合
     * @param noActivityList 订单未参与满减活动的商品集合
     * @param couponPrice    订单使用优惠券优惠的金额
     * @return
     */
    private int getCountType(List<OrderSkuVO> activityList, List<OrderSkuVO> noActivityList, Double couponPrice) {
        if (activityList.size() == 0) {
            if (couponPrice == 0) {
                return 1;
            } else {
                return 2;
            }
        } else {
            if (couponPrice == 0) {
                if (noActivityList.size() == 0) {
                    return 3;
                } else {
                    return 4;
                }
            } else {
                if (noActivityList.size() == 0) {
                    return 5;
                } else {
                    return 6;
                }
            }
        }
    }

    /**
     * 对要操作的订单进行权限检查
     *
     * @param permission 需要的权限
     * @param order      相应的订单
     */
    private void checkPermission(OrderPermission permission, OrderDetailVO order) {

        if (permission != null) {
            if (order == null) {
                throw new NoPermissionException("无权操作此订单");
            }

            // 校验卖家权限
            if (permission.equals(OrderPermission.seller)) {
                Seller seller = UserContext.getSeller();
                if (seller == null || !seller.getSellerId().equals(order.getSellerId())) {
                    throw new NoPermissionException("无权操作此订单");
                }
            }

            // 校验买家权限
            if (permission.equals(OrderPermission.buyer)) {
                Buyer buyer = UserContext.getBuyer();
                if (buyer == null || buyer.getUid() == null
                        || !buyer.getUid().equals(order.getMemberId())) {
                    throw new NoPermissionException("无权操作此订单");
                }
            }

            // 校验管理权限
            if (permission.equals(OrderPermission.admin)) {

            }

            // 目前客户端不用校栓任何权限
            if (permission.equals(OrderPermission.client)) {

            }

        }
    }


    /**
     * 进行可操作校验
     * 看此状态下是否允许此操作
     *
     * @param order
     * @param orderOperate
     */
    private void checkAllowable(OrderPermission permission, OrderDetailVO order, OrderOperateEnum orderOperate) {
        //如果是client权限，则不验证下一步操作
        if (OrderPermission.client.equals(permission)) {
            return;
        }

        OrderStatusEnum status = OrderStatusEnum.valueOf(order.getOrderStatus());

        String flowType = order.getOrderType();
        //如果是普通订单或者是售后服务生成的新订单则取订单支付类型
        if (OrderTypeEnum.NORMAL.name().equals(flowType) || OrderTypeEnum.CHANGE.name().equals(flowType) || OrderTypeEnum.SUPPLY_AGAIN.name().equals(flowType)) {
            flowType = order.getPaymentType();
        }
        boolean isAllow = FlowCheckOperate.checkOperate(flowType, status.name(), orderOperate.name());

        if (!isAllow) {
            throw new ServiceException(TradeErrorCode.E460.code(), "订单" + status.description() + "状态不能进行" + orderOperate.description() + "操作");
        }

    }


    /**
     * 内部类，为了传递参数使用
     */
    private class PayParam {
        private Double payPrice;
        private String returnTradeNo;

        public Double getPayPrice() {
            return payPrice;
        }

        public String getReturnTradeNo() {
            return returnTradeNo;
        }

        public void setPayPrice(Double payPrice) {
            this.payPrice = payPrice;
        }

        public void setReturnTradeNo(String returnTradeNo) {
            this.returnTradeNo = returnTradeNo;
        }
    }


}
