package com.enation.app.javashop.consumer.shop.member;

import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.consumer.core.event.TradeIntoDbEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.member.MemberHistoryReceiptClient;
import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.member.enums.ReceiptTypeEnum;
import com.enation.app.javashop.model.member.vo.ReceiptHistoryVO;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改会员开票历史记录消费者
 *
 * @author duanmingyu
 * @version v7.1.4
 * @since v7.0.0
 * 2019-06-24
 */
@Service
public class MemberReceiptStatusConsumer implements OrderStatusChangeEvent,TradeIntoDbEvent{

    @Autowired
    private MemberHistoryReceiptClient memberHistoryReceiptClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        //获取订单信息
        OrderDO order = orderMessage.getOrderDO();

        //出库成功
        if(orderMessage.getNewStatus().name().equals(OrderStatusEnum.CONFIRM.name())){
            //如果订单需要开具发票
            if (order.getNeedReceipt().intValue() == 1) {
                ReceiptHistoryVO receiptHistoryVO = memberHistoryReceiptClient.getReceiptHistory(order.getSn());
                ReceiptHistory receiptHistory = new ReceiptHistory();
                BeanUtil.copyProperties(receiptHistoryVO, receiptHistory);
                receiptHistory.setOrderStatus(OrderStatusEnum.CONFIRM.value());
                //如果订单开具的是增值税普通发票
                if (ReceiptTypeEnum.VATORDINARY.value().equals(receiptHistoryVO.getReceiptType())) {
                    receiptHistory.setMemberName(order.getShipName());
                    receiptHistory.setMemberMobile(order.getShipMobile());
                    receiptHistory.setProvinceId(order.getShipProvinceId());
                    receiptHistory.setCityId(order.getShipCityId());
                    receiptHistory.setCountyId(order.getShipCountyId());
                    receiptHistory.setTownId(order.getShipTownId());
                    receiptHistory.setProvince(order.getShipProvince());
                    receiptHistory.setCity(order.getShipCity());
                    receiptHistory.setCounty(order.getShipCounty());
                    receiptHistory.setTown(order.getShipTown());
                    receiptHistory.setDetailAddr(order.getShipAddr());
                }

                this.memberHistoryReceiptClient.edit(receiptHistory);
                logger.debug("发票信息确认完成");
            }
            return;
        }


        //发货
        if (orderMessage.getNewStatus().name().equals(OrderStatusEnum.SHIPPED.name())) {

            //如果订单需要开具发票
            if (order.getNeedReceipt().intValue() == 1) {
                ReceiptHistoryVO receiptHistoryVO = memberHistoryReceiptClient.getReceiptHistory(order.getSn());
                //如果订单开具的是增值税普通发票
                if (ReceiptTypeEnum.VATORDINARY.value().equals(receiptHistoryVO.getReceiptType())) {
                    ReceiptHistory receiptHistory = new ReceiptHistory();
                    BeanUtil.copyProperties(receiptHistoryVO, receiptHistory);

                    //设置普通发票物流信息并将发票记录状态设置为已开票
                    receiptHistory.setLogiId(order.getLogiId());
                    receiptHistory.setLogiName(order.getLogiName());
                    receiptHistory.setLogiCode(order.getShipNo());
                    receiptHistory.setStatus(1);
                    this.memberHistoryReceiptClient.edit(receiptHistory);

                    logger.debug("发票信息开票完成");
                }
            }
        }

    }

    @Override
    public void onTradeIntoDb(TradeVO tradeVO) {
        //从交易中获取订单列表
        List<OrderDTO> orderDTOS = tradeVO.getOrderList();
        //循环订单取出发票信息
        for (OrderDTO orderDTO : orderDTOS) {
            //如果订单需要开具发票
            if (orderDTO.getNeedReceipt().intValue() == 1) {
                ReceiptHistory receiptHistory = orderDTO.getReceiptHistory();
                if (receiptHistory != null && receiptHistory.getReceiptTitle() != null && receiptHistory.getReceiptType() != null) {
                    receiptHistory.setAddTime(DateUtil.getDateline());
                    receiptHistory.setMemberId(orderDTO.getMemberId());
                    receiptHistory.setUname(orderDTO.getMemberName());
                    receiptHistory.setOrderSn(orderDTO.getSn());
                    receiptHistory.setOrderPrice(orderDTO.getOrderPrice());
                    receiptHistory.setSellerId(orderDTO.getSellerId());
                    receiptHistory.setSellerName(orderDTO.getSellerName());
                    //绑定此发票的订单的出库状态
                    receiptHistory.setOrderStatus(OrderStatusEnum.NEW.value());
                    receiptHistory.setStatus(0);
                    //订单中的商品数据
                    receiptHistory.setGoodsJson(JsonUtil.objectToJson(orderDTO.getOrderSkuList()));
                    memberHistoryReceiptClient.add(receiptHistory);
                    logger.debug("发票信息入库完成");
                }
            }
        }
    }
}
