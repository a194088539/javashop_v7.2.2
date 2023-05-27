package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.TransactionRecord;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单确认收货增加交易记录消费者
 * @author Snow create in 2018/5/22
 * @version v2.0
 * @since v7.0.0
 */
@Component
public class OrderRogTransactionConsumer implements OrderStatusChangeEvent {

    @Autowired
    private OrderOperateClient orderOperateClient;

    @Override
    @Transactional(value = "tradeTransactionManager",propagation = Propagation.REQUIRED,rollbackFor = {Exception.class})
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        if(orderMessage.getNewStatus().equals(OrderStatusEnum.ROG)){

            OrderDO orderDO = orderMessage.getOrderDO();
            //换货订单或者补发商品订单不产生购买记录
            if(OrderTypeEnum.CHANGE.name().equals(orderDO.getOrderType())
                    ||OrderTypeEnum.SUPPLY_AGAIN.name().equals(orderDO.getOrderType())){
                return;
            }

            TransactionRecord record = new TransactionRecord();
            record.setOrderSn(orderDO.getSn());
            if(orderDO.getMemberId()==null){
                record.setUname("游客");
                record.setMemberId(0L);
            }else{
                record.setMemberId(orderDO.getMemberId());
                record.setUname(orderDO.getMemberName());
            }
            record.setRogTime(DateUtil.getDateline());
            String itemJson = orderDO.getItemsJson();

            List<OrderSkuVO> orderSkuVOList = JsonUtil.jsonToList(itemJson, OrderSkuVO.class);

            for (OrderSkuVO orderSkuVO : orderSkuVOList) {
                record.setPrice(orderSkuVO.getPurchasePrice());
                record.setGoodsNum(orderSkuVO.getNum());
                record.setGoodsId(orderSkuVO.getGoodsId());
                record.setCreateTime(orderDO.getCreateTime());
                orderOperateClient.addTransactionRecord(record);
            }
        }

    }

}
