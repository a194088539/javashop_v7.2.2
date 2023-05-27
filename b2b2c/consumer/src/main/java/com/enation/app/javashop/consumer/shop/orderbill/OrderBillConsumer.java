package com.enation.app.javashop.consumer.shop.orderbill;

import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.consumer.core.event.ASNewOrderEvent;
import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.aftersale.enums.ServiceStatusEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceTypeEnum;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.trade.BillClient;
import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.model.orderbill.dos.BillItem;
import com.enation.app.javashop.model.orderbill.enums.BillType;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 结算单消费者
 * @date 2018/4/26 15:00
 * @since v7.0.0
 */
@Service
public class OrderBillConsumer implements OrderStatusChangeEvent, ASNewOrderEvent, AfterSaleChangeEvent {

    @Autowired
    private BillClient billClient;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private CouponClient couponClient;
    @Autowired
    private AfterSaleClient afterSaleClient;

    /**
     * 订单收货生成一条结算单
     *
     * @param orderMessage
     */
    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        //收货后生成结算单项
        if (orderMessage.getNewStatus().equals(OrderStatusEnum.PAID_OFF)) {

            BillItem billItem = getBillItem(orderMessage.getOrderDO().getSn(), BillType.PAYMENT);

            this.billClient.add(billItem);
        }

    }

    /**
     * 退货售后服务审核通过后生成一条结算单
     * @param afterSaleChangeMessage
     */
    @Override
    public void afterSaleChange(AfterSaleChangeMessage afterSaleChangeMessage) {

        //如果售后服务单状态是已完成 并且 (售后服务类型为退货 或者 取消订单）
        boolean flag = ServiceStatusEnum.COMPLETED.equals(afterSaleChangeMessage.getServiceStatus()) &&
                (ServiceTypeEnum.RETURN_GOODS.equals(afterSaleChangeMessage.getServiceType()) || ServiceTypeEnum.ORDER_CANCEL.equals(afterSaleChangeMessage.getServiceType()));

        if (flag) {

            RefundDO refundDO = this.afterSaleClient.getAfterSaleRefundModel(afterSaleChangeMessage.getServiceSn());

            if (refundDO != null) {
                BillItem billItem = getBillItem(refundDO.getOrderSn(), BillType.REFUND);
                billItem.setRefundSn(refundDO.getSn());
                billItem.setRefundTime(refundDO.getCreateTime());
                billItem.setPrice(refundDO.getActualPrice());
                this.billClient.add(billItem);
            }
        }
    }

    /**
     * 构造一个结算单项
     *
     * @param orderSn
     * @param billType
     * @return
     */
    private BillItem getBillItem(String orderSn, BillType billType) {

        OrderDetailDTO orderDetail = orderClient.getModel(orderSn);

        Double orderPrice = orderDetail.getOrderPrice();
        Double discountPrice = orderDetail.getDiscountPrice();
        //查询订单使用的优惠券金额
        List<GoodsCouponPrice> goodsCouponPrices = orderDetail.getGoodsCouponPrices();
        //一个订单中只能使用一个优惠券，要么平台的，要么店铺的，所以，累计优惠券金额就可以
        Double siteCouponPrice = 0D;
        Double couponCommission = 0D;

        CouponDO coupon = null;
        if(goodsCouponPrices != null && goodsCouponPrices.size() != 0){
            for (GoodsCouponPrice goodsCouponPrice : goodsCouponPrices) {
                if(coupon == null){
                    coupon = couponClient.getModel(goodsCouponPrice.getCouponId());
                }

                if(coupon.getSellerId()!=0){
                    //非站点优惠券
                    break;
                }

                siteCouponPrice = CurrencyUtil.add(siteCouponPrice, goodsCouponPrice.getCouponPrice());
                couponCommission = CurrencyUtil.div(coupon.getShopCommission(),100);
            }
        }

        BillItem item = new BillItem();
        item.setAddTime(DateUtil.getDateline());
        item.setItemType(billType.name());
        //未出账
        item.setStatus(0);
        item.setOrderSn(orderSn);
        item.setPrice(orderPrice);
        item.setDiscountPrice(discountPrice);
        item.setSellerId(orderDetail.getSellerId());
        item.setMemberId(orderDetail.getMemberId());
        item.setMemberName(orderDetail.getMemberName());
        item.setOrderTime(orderDetail.getCreateTime());
        item.setPaymentType(orderDetail.getPaymentType());
        item.setShipName(orderDetail.getShipName());
        item.setCouponCommission(couponCommission);
        item.setSiteCouponPrice(siteCouponPrice);
        return item;
    }

}
