package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.consumer.core.event.ShopCollectionEvent;
import com.enation.app.javashop.consumer.core.event.ShopStatusChangeEvent;
import com.enation.app.javashop.model.base.message.ShopStatusChangeMsg;
import com.enation.app.javashop.client.member.ShopClient;
import com.enation.app.javashop.client.statistics.GoodsDataClient;
import com.enation.app.javashop.client.statistics.ShopDataClient;
import com.enation.app.javashop.model.shop.enums.ShopStatusEnum;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 店铺数据收集
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-23 下午5:50
 */
@Component
public class DataShopConsumer implements ShopCollectionEvent, ShopStatusChangeEvent {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ShopClient shopClient;

    @Autowired
    private ShopDataClient shopDataClient;

    @Autowired
    private GoodsDataClient goodsDataClient;

    /**
     * 店铺变更
     *
     * @param shopId
     */
    @Override
    public void shopChange(Long shopId) {

        try {
            ShopVO shop = shopClient.getShop(shopId);
            ShopData shopData = new ShopData(shop);
            this.shopDataClient.add(shopData);
        } catch (Exception e) {
            logger.error("店铺消息收集失败", e);
        }
    }

    /**
     * 状态变更
     *
     * @param shopStatusChangeMsg 店铺信息
     */
    @Override
    public void changeStatus(ShopStatusChangeMsg shopStatusChangeMsg) {
        if (shopStatusChangeMsg.getStatusEnum().equals(ShopStatusEnum.CLOSED)) {
            goodsDataClient.underAllGoods(shopStatusChangeMsg.getSellerId());
        }
    }
}
