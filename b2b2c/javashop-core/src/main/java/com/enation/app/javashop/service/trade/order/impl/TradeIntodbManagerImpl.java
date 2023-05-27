package com.enation.app.javashop.service.trade.order.impl;

import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.mapper.trade.order.OrderItemsMapper;
import com.enation.app.javashop.mapper.trade.order.OrderMapper;
import com.enation.app.javashop.mapper.trade.order.TradeMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.trade.cart.vo.CartPromotionVo;
import com.enation.app.javashop.model.trade.order.dos.*;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import com.enation.app.javashop.service.trade.order.OrderLogManager;
import com.enation.app.javashop.service.trade.order.OrderOutStatusManager;
import com.enation.app.javashop.service.trade.order.TradeIntodbManager;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description: 交易入库业务类
 * @author: liuyulei
 * @create: 2020/3/20 15:29
 * @version:1.0
 * @since: 7.2.0
 **/
@Service
public class TradeIntodbManagerImpl implements TradeIntodbManager {

    @Autowired
    private TradeMapper tradeMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemsMapper orderItemsMapper;

    @Autowired
    private Cache cache;

    @Autowired
    private MessageSender messageSender;

    private Integer orderCacheTimeout = 60 * 60;

    @Autowired
    private OrderLogManager orderLogManager;

    @Autowired
    private OrderOutStatusManager orderOutStatusManager;

    @Autowired
    private GoodsClient goodsClient;

    @Override
    @Transactional(value = "tradeTransactionManager", propagation = Propagation.REQUIRED,
            rollbackFor = {RuntimeException.class, ServiceException.class, Exception.class})
    public void intoDB(TradeVO tradeVO) {
        if (tradeVO == null) {
            throw new RuntimeException("交易无法入库，原因：trade为空");
        }
        // 交易入库
        TradeDO tradeDO = new TradeDO(tradeVO);
        tradeMapper.insert(tradeDO);

        // 订单入库
        this.innoDBOrder(tradeVO.getOrderList());

        String cacheKey = getTradeCacheKey(tradeVO.getTradeSn());
        this.cache.put(cacheKey, tradeVO, orderCacheTimeout);
        //发送订单创建消息
        this.messageSender.send(new MqMessage(AmqpExchange.ORDER_CREATE,
                AmqpExchange.ORDER_CREATE + "_ROUTING",
                cacheKey));

    }

    /**
     * 订单入库
     *
     * @param orderDTOList
     */
    private void innoDBOrder(List<OrderDTO> orderDTOList) {

        for (OrderDTO order : orderDTOList) {
            //订单mq消息
            OrderStatusChangeMsg orderStatusChangeMsg = new OrderStatusChangeMsg();

            //将DTO转换DO
            OrderDO orderDO = new OrderDO(order);
            orderDO.setTradeSn(order.getTradeSn());
            orderDO.setOrderStatus(OrderStatusEnum.NEW.value());
            orderStatusChangeMsg.setOldStatus(OrderStatusEnum.NEW);
            orderStatusChangeMsg.setNewStatus(OrderStatusEnum.NEW);

            // 为orderDTO 赋默认值，这些值会在orderLineVO中使用
            order.setOrderStatus(orderDO.getOrderStatus());
            order.setPayStatus(orderDO.getPayStatus());
            order.setShipStatus(orderDO.getShipStatus());
            order.setCommentStatus(orderDO.getCommentStatus());
            order.setServiceStatus(orderDO.getServiceStatus());

            orderMapper.insert(orderDO);

            //订单项入库
            for (OrderSkuVO skuVO : order.getOrderSkuList()) {
                GoodsSkuVO goodsSkuVO = this.goodsClient.getSkuFromCache(skuVO.getSkuId());
                OrderItemsDO item = new OrderItemsDO(goodsSkuVO);
                item.setOrderSn(orderDO.getSn());
                item.setTradeSn(orderDO.getTradeSn());
                item.setNum(skuVO.getNum());
                item.setPrice(skuVO.getPurchasePrice());
                //判断是否为团购优惠
                List<CartPromotionVo> singleList = skuVO.getSingleList();
                if (singleList != null && !singleList.isEmpty()) {
                    for (CartPromotionVo cartPromotionVo : singleList) {
                        if (PromotionTypeEnum.GROUPBUY.toString().equals(cartPromotionVo.getPromotionType())) {
                            item.setPromotionType(cartPromotionVo.getPromotionType());
                            item.setPromotionId(cartPromotionVo.getPromotionId());
                            break;
                        }
                    }
                }
                orderItemsMapper.insert(item);
            }

            //发送amqp状态消息
            orderStatusChangeMsg.setOrderDO(orderDO);

            //发送订单创建消息
            this.messageSender.send(new MqMessage(AmqpExchange.ORDER_STATUS_CHANGE, AmqpExchange.ORDER_STATUS_CHANGE + "_ROUTING", orderStatusChangeMsg));
        }
    }


    /**
     * 获取交易缓存key
     *
     * @param tradeSn
     * @return
     */
    public String getTradeCacheKey(String tradeSn) {
        //重新压入缓存
        String cacheKey = CachePrefix.TRADE_SESSION_ID_PREFIX.getPrefix() + tradeSn;
        return cacheKey;
    }


}
