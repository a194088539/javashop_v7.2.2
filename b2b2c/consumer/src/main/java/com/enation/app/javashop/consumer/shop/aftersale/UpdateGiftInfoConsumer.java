package com.enation.app.javashop.consumer.shop.aftersale;

import com.enation.app.javashop.client.promotion.FullDiscountGiftClient;
import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.client.trade.OrderMetaClient;
import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
import com.enation.app.javashop.model.aftersale.enums.ServiceStatusEnum;
import com.enation.app.javashop.model.aftersale.enums.ServiceTypeEnum;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.ShipStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 修改订单赠送的赠品售后状态和库存相关信息
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-12-26
 */
@Component
public class UpdateGiftInfoConsumer implements AfterSaleChangeEvent {

    @Autowired
    private AfterSaleClient afterSaleClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private OrderMetaClient orderMetaClient;

    @Autowired
    private FullDiscountGiftClient fullDiscountGiftClient;

    @Override
    public void afterSaleChange(AfterSaleChangeMessage afterSaleChangeMessage) {

        //获取售后服务单详细信息
        ApplyAfterSaleVO applyAfterSaleVO = this.afterSaleClient.detail(afterSaleChangeMessage.getServiceSn());
        //获取售后服务单状态
        ServiceStatusEnum serviceStatus = afterSaleChangeMessage.getServiceStatus();
        //获取售后服务类型
        ServiceTypeEnum serviceType = afterSaleChangeMessage.getServiceType();
        //获取申请售后的订单号
        String orderSn = applyAfterSaleVO.getOrderSn();

        //只有售后服务类型为取消订单和退货才涉及到订单赠品状态和库存的修改
        boolean flag = ServiceTypeEnum.ORDER_CANCEL.equals(serviceType) || ServiceTypeEnum.RETURN_GOODS.equals(serviceType);

        if (flag) {
            //如果售后服务状态为待审核（证明是新提交的售后服务申请）
            if (ServiceStatusEnum.APPLY.equals(serviceStatus)) {

                //获取订单未申请售后的赠品信息集合
                List<FullDiscountGiftDO> giftList = this.orderMetaClient.getGiftList(orderSn, OrderServiceStatusEnum.NOT_APPLY.value());

                //如果订单赠品信息集合不为空
                if (giftList != null) {
                    //将订单赠品售后状态修改为售后服务单号，用来和售后服务进行关联（暂时这样处理，后期需要将赠品信息存储在售后服务信息中）
                    this.orderMetaClient.updateMetaStatus(orderSn, OrderMetaKeyEnum.GIFT, applyAfterSaleVO.getSn());

                }
            }

            //如果售后服务申请未通过商家审核
            if (ServiceStatusEnum.REFUSE.equals(serviceStatus)) {

                //获取当前售后服务单关联的并且已申请售后的订单赠品信息集合（注意：由于订单中可能购买了多个商品，为了保证赠品
                // 和售后服务单一对一，在申请售后时，会将es_order_meta表中的status状态值修改为售后单号，因此此处需要用售后单号获取赠品信息）
                List<FullDiscountGiftDO> giftList = this.orderMetaClient.getGiftList(orderSn, applyAfterSaleVO.getSn());

                //如果订单赠品信息集合不为空
                if (giftList != null) {
                    //将订单赠品售后状态恢复为未申请
                    this.orderMetaClient.updateMetaStatus(orderSn, OrderMetaKeyEnum.GIFT, OrderServiceStatusEnum.NOT_APPLY.value());

                }

            }

            //如果售后服务已完成，需要恢复赠品的库存
            if (ServiceStatusEnum.COMPLETED.equals(serviceStatus)) {

                //获取当前售后服务单关联的并且已申请售后的订单赠品信息集合（注意：由于订单中可能购买了多个商品，为了保证赠品
                // 和售后服务单一对一，在申请售后时，会将es_order_meta表中的status状态值修改为售后单号，因此此处需要用售后单号获取赠品信息）
                List<FullDiscountGiftDO> giftList = this.orderMetaClient.getGiftList(orderSn, applyAfterSaleVO.getSn());

                //如果订单赠品信息集合不为空
                if (giftList != null) {
                    //获取订单信息
                    OrderDO orderDO = this.orderClient.getOrder(orderSn);

                    //如果订单发货状态为未发货，则只需恢复赠品的可用库存即可；否则其它状态（已发货、已收货）则需要恢复赠品的实际库存和可用库存
                    if (!ShipStatusEnum.SHIP_NO.value().equals(orderDO.getShipStatus())) {
                        this.fullDiscountGiftClient.addGiftQuantity(giftList);
                    }
                }
            }
        }
    }
}
