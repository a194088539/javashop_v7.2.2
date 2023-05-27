package com.enation.app.javashop.consumer.shop.promotion;

import com.enation.app.javashop.client.trade.PintuanGoodsClient;
import com.enation.app.javashop.consumer.core.event.GoodsSkuChangeEvent;
import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 删除促销活动商品
 *
 * @author liuyulei
 * @version v1.0
 * @since v7.1.3 2019-07-30
 */
@Component
public class PromotionGoodsChangeConsumer implements GoodsSkuChangeEvent {

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;

    @Autowired
    private PintuanGoodsClient pintuanGoodsClient;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void goodsSkuChange(List<Long> delSkuIds) {

        promotionGoodsClient.delPromotionGoods(delSkuIds);
        pintuanGoodsClient.deletePinTuanGoods(delSkuIds);

    }

}
