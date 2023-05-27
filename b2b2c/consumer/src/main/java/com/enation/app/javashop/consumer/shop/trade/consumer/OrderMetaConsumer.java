package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.client.promotion.FullDiscountGiftClient;
import com.enation.app.javashop.client.trade.OrderMetaClient;
import com.enation.app.javashop.client.payment.PayLogClient;
import com.enation.app.javashop.consumer.core.event.TradeIntoDbEvent;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.sncreator.SnCreator;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.base.SubCode;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.promotion.tool.vo.GiveGiftVO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单其他信息入库
 *
 * @author Snow create in 2018/6/26
 * @version v2.0
 * @since v7.0.0
 */
@Component
@Order(1)
public class OrderMetaConsumer implements TradeIntoDbEvent {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderMetaClient orderMetaClient;


    @Autowired
    SnCreator snCreator;

    @Autowired
    private FullDiscountGiftClient fullDiscountGiftClient;

    @Autowired
    private CouponClient couponClient;

    @Autowired
    private PayLogClient payLogClient;

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void onTradeIntoDb(TradeVO tradeVO) {

        List<OrderDTO> orderList = tradeVO.getOrderList();
        //获取使用的优惠券，根据商家匹配到相应商家中
        for (OrderDTO orderDTO : orderList) {
            addCashBack(orderDTO);
            addUsePoint(orderDTO);
            addGiftPoint(orderDTO);
            addGiftCoupon(orderDTO);
            addCouponPrice(orderDTO);
            addGift(orderDTO);
            addLog(orderDTO);
            addFullMinusPrice(orderDTO);
        }

    }



    /**
     * 记录返现金额
     *
     * @param orderDTO
     */
    private void addCashBack(OrderDTO orderDTO) {
        try {
            //记录返现金额
            double cashBack = orderDTO.getPrice().getCashBack();
            OrderMetaDO cashBackMeta = new OrderMetaDO();
            cashBackMeta.setMetaKey(OrderMetaKeyEnum.CASH_BACK.name());
            cashBackMeta.setMetaValue(cashBack + "");
            cashBackMeta.setOrderSn(orderDTO.getSn());
            this.orderMetaClient.add(cashBackMeta);
            logger.debug("返现金额入库完成");
        } catch (Exception e) {
            logger.error("返现金额入库出错", e);
        }

    }


    /**
     * 记录使用的积分
     *
     * @param orderDTO
     */
    private void addUsePoint(OrderDTO orderDTO) {
        try {

            //使用的积分
            Long consumerPoint = orderDTO.getPrice().getExchangePoint();
            if (consumerPoint > 0) {
                //使用积分的记录
                OrderMetaDO pointMeta = new OrderMetaDO();
                pointMeta.setMetaKey(OrderMetaKeyEnum.POINT.name());
                pointMeta.setMetaValue(consumerPoint + "");
                pointMeta.setOrderSn(orderDTO.getSn());
                pointMeta.setStatus(OrderServiceStatusEnum.NOT_APPLY.name());
                this.orderMetaClient.add(pointMeta);
            }

            logger.debug("记录使用的积分");

        } catch (Exception e) {
            logger.error("记录使用的积分出错", e);
        }

    }


    /**
     * 赠送积分的记录
     *
     * @param orderDTO
     */
    private void addGiftPoint(OrderDTO orderDTO) {
        try {
            if(StringUtil.isEmpty(orderDTO.getGiftJson())){
                return;
            }
            //赠品入库
            List<GiveGiftVO> giftList= JsonUtil.jsonToList(orderDTO.getGiftJson(), GiveGiftVO.class);
            Integer givePoint = 0;
            for (GiveGiftVO giveGiftVO:giftList) {
                if("point".equals(giveGiftVO.getType())){
                    givePoint += (Integer)giveGiftVO.getValue();
                }
            }
            //赠送积分的记录
            OrderMetaDO giftPointMeta = new OrderMetaDO();
            giftPointMeta.setMetaKey(OrderMetaKeyEnum.GIFT_POINT.name());
            giftPointMeta.setMetaValue(givePoint + "");
            giftPointMeta.setOrderSn(orderDTO.getSn());
            giftPointMeta.setStatus(OrderServiceStatusEnum.NOT_APPLY.name());
            this.orderMetaClient.add(giftPointMeta);

            logger.debug("赠送积分完成");
        } catch (Exception e) {
            logger.error("赠送积分的记录出错", e);
        }

    }


    /**
     * 赠优惠券入库
     *
     * @param orderDTO
     */
    private void addGiftCoupon(OrderDTO orderDTO) {
        try {
            if(StringUtil.isEmpty(orderDTO.getGiftJson())){
                return;
            }
            //赠品入库
            List<GiveGiftVO> giftList= JsonUtil.jsonToList(orderDTO.getGiftJson(), GiveGiftVO.class);
            List<CouponDO> couponList = new ArrayList<>();
            for (GiveGiftVO giveGiftVO:giftList) {
                if("coupon".equals(giveGiftVO.getType())){
                    couponList.add(couponClient.getModel(Long.valueOf(giveGiftVO.getValue().toString())));
                }
            }

            OrderMetaDO couponMeta = new OrderMetaDO();
            couponMeta.setMetaKey(OrderMetaKeyEnum.COUPON.name());
            couponMeta.setMetaValue(JsonUtil.objectToJson(couponList));
            couponMeta.setOrderSn(orderDTO.getSn());
            couponMeta.setStatus(OrderServiceStatusEnum.NOT_APPLY.name());
            this.orderMetaClient.add(couponMeta);

            logger.debug("赠优惠券完成");

        } catch (Exception e) {
            logger.error("赠优惠券出错", e);
        }

    }


    /**
     * 记录使用优惠券的金额
     *
     * @param orderDTO
     */
    private void addCouponPrice(OrderDTO orderDTO) {
        try {
            List<GoodsCouponPrice> couponList = orderDTO.getGoodsCouponPrices();
            //记录使用优惠券的金额
            OrderMetaDO orderMeta = new OrderMetaDO();
            orderMeta.setMetaKey(OrderMetaKeyEnum.COUPON_PRICE.name());
            orderMeta.setMetaValue(JsonUtil.objectToJson(couponList));
            orderMeta.setOrderSn(orderDTO.getSn());
            orderMetaClient.add(orderMeta);

            logger.debug("使用优惠券的金额完成");

        } catch (Exception e) {
            logger.error("使用优惠券的金额出错", e);
        }

    }


    /**
     * 记录使用优惠券的金额
     *
     * @param orderDTO
     */
    private void addFullMinusPrice(OrderDTO orderDTO) {
        try {

            //记录使用优惠券的金额
            OrderMetaDO orderMeta = new OrderMetaDO();
            orderMeta.setMetaKey(OrderMetaKeyEnum.FULL_MINUS.name());
            orderMeta.setMetaValue("" + orderDTO.getPrice().getFullMinus());
            orderMeta.setOrderSn(orderDTO.getSn());
            orderMetaClient.add(orderMeta);
            logger.debug("订单满减金额入库完成");
        } catch (Exception e) {
            logger.error("订单满减金额入库出错", e);
        }

    }


    /***
     * 赠品入库
     * @param orderDTO
     */
    private void addGift(OrderDTO orderDTO) {

        try {
            if(StringUtil.isEmpty(orderDTO.getGiftJson())){
                return;
            }

            //赠品入库
            List<GiveGiftVO> giftList= JsonUtil.jsonToList(orderDTO.getGiftJson(), GiveGiftVO.class);
            List<FullDiscountGiftDO> giftLists = new ArrayList<>();
            for (GiveGiftVO giveGiftVO:giftList) {
                if("gift".equals(giveGiftVO.getType())){
                    giftLists.add(fullDiscountGiftClient.getModel(Long.valueOf(giveGiftVO.getValue().toString())));
                }
            }
            OrderMetaDO giftMeta = new OrderMetaDO();
            giftMeta.setMetaKey(OrderMetaKeyEnum.GIFT.name());
            giftMeta.setMetaValue(JsonUtil.objectToJson(giftLists));
            giftMeta.setOrderSn(orderDTO.getSn());
            giftMeta.setStatus(OrderServiceStatusEnum.NOT_APPLY.name());
            this.orderMetaClient.add(giftMeta);

            logger.debug("赠品入库完成");

        } catch (Exception e) {
            logger.error("赠品入库出错", e);
        }


    }


    /**
     * 记录日志
     *
     * @param orderDTO
     */
    private void addLog(OrderDTO orderDTO) {
        try {
            //付款单
            PayLog payLog = new PayLog();
            payLog.setPayLogSn(""+snCreator.create(SubCode.PAY_BILL));
            payLog.setOrderSn(orderDTO.getSn());
            payLog.setPayMemberName(orderDTO.getMemberName());
            payLog.setPayStatus(PayStatusEnum.PAY_NO.name());
            payLog.setPayWay(orderDTO.getPaymentType());

            this.payLogClient.add(payLog);

            logger.debug("日志入库完成");

        } catch (Exception e) {
            logger.error("日志入库出错", e);
        }

    }

}
