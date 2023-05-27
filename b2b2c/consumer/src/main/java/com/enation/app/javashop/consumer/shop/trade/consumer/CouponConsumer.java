package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.client.trade.OrderMetaClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.consumer.core.event.TradeIntoDbEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 确认收款发放促销活动赠送优惠券
 *
 * @author Snow create in 2018/5/22
 * @version v2.0
 * @since v7.0.0
 */
@Component
public class CouponConsumer implements OrderStatusChangeEvent, TradeIntoDbEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderMetaClient orderMetaClient;

    @Autowired
    private MemberClient memberClient;

    @Autowired
    private CouponClient couponClient;


    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        if ((orderMessage.getNewStatus().name()).equals(OrderStatusEnum.PAID_OFF.name())) {

            OrderDO order = orderMessage.getOrderDO();

            //读取已发放的优惠券json
            String itemJson = this.orderMetaClient.getMetaValue(order.getSn(), OrderMetaKeyEnum.COUPON);
            List<CouponDO> couponList = JsonUtil.jsonToList(itemJson, CouponDO.class);

            if (couponList != null && couponList.size() > 0) {
                // 循环发放的优惠券
                for (CouponDO coupon : couponList) {
                    //获取当前数据库中优惠券的数据信息
                    CouponDO couponDO = this.couponClient.getModel(coupon.getCouponId());
                    //优惠券可领取数量不足时,不赠送优惠券
                    if (CurrencyUtil.sub(couponDO.getCreateNum(), couponDO.getReceivedNum()) <= 0) {
                        continue;
                    }
                    this.memberClient.receiveBonus(order.getMemberId(), order.getMemberName(), coupon.getCouponId());
                }
            }
        }
    }


    @Override
    public void onTradeIntoDb(TradeVO tradeVO) {
        try {

            List<OrderDTO> orderDTOList = tradeVO.getOrderList();

            //用于记录使用的优惠券id，重复的不记录，使用set的特性过滤
            Set<Long> couponIdSet = new HashSet();
            for (OrderDTO orderDTO : orderDTOList) {

                List<GoodsCouponPrice> couponList = orderDTO.getGoodsCouponPrices();
                if (couponList != null) {
                    for (GoodsCouponPrice goodsCouponPrice : couponList) {
                        couponIdSet.add(goodsCouponPrice.getMemberCouponId());
                    }
                }
            }

            for (Long id : couponIdSet) {
                //使用优惠券
                this.memberClient.usedCoupon(id, "");
                //查询该使用了的优惠券
                MemberCoupon memberCoupon = this.memberClient.getModel(tradeVO.getMemberId(), id);
                //修改店铺已经使用优惠券数量
                this.couponClient.addUsedNum(memberCoupon.getCouponId());
            }

            logger.debug("更改优惠券的状态完成");

        } catch (Exception e) {
            logger.error("更改优惠券的状态出错", e);
        }
    }
}
