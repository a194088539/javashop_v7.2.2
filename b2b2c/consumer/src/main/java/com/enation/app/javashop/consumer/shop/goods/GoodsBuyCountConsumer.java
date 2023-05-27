package com.enation.app.javashop.consumer.shop.goods;

import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fk
 * @version v1.0
 * @Description: 商品购买数量变化
 * @date 2018/6/2510:13
 * @since v7.0.0
 */
@Service
public class GoodsBuyCountConsumer implements OrderStatusChangeEvent {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private OrderClient orderClient;
    @Autowired
    private PromotionGoodsClient groupbuyGoodsClient;


    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {
        //进行团购商品售卖数量增加
        if (orderMessage.getNewStatus().name().equals(OrderStatusEnum.NEW.name())) {
            //获取交易id
            String orderSn = orderMessage.getOrderDO().getSn();
            if (!orderSn.isEmpty()) {
                //截取id值
                //根据ID找到商品信息
                List<Map> list = orderClient.getItemsPromotionTypeandNum(orderSn);
                if (!list.isEmpty()) {
                    for (Map<String, Object> map : list) {
                        //根据商品类型进行判断
                        if (map.get("promotion_type") != null && PromotionTypeEnum.GROUPBUY.toString().equals(map.get("promotion_type").toString())) {
                            String goodId = (map.get("goods_id").toString());
                            String num = (map.get("num").toString());
                            String productId = (map.get("product_id").toString());
                            if (!goodId.isEmpty() && !num.isEmpty()) {
                                //更新库存
                                groupbuyGoodsClient.renewGroupbuyBuyNum(Long.parseLong(goodId), Integer.parseInt(num), Long.parseLong(productId));
                            }
                            //如果不是团购商品，打断循环退出
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        // 收货后更新商品的购买数量
        if (orderMessage.getNewStatus().name().equals(OrderStatusEnum.ROG.name())) {
            OrderDO order = orderMessage.getOrderDO();
            String itemsJson = order.getItemsJson();
            List<OrderSkuVO> list = JsonUtil.jsonToList(itemsJson, OrderSkuVO.class);
            this.goodsClient.updateBuyCount(list);
        }
    }
}
