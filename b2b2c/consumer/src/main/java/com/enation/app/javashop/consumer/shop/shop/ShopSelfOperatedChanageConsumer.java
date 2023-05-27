package com.enation.app.javashop.consumer.shop.shop;

import com.enation.app.javashop.consumer.core.event.ShopChangeEvent;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.promotion.ExchangeGoodsClient;
import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.shop.enums.ShopMessageTypeEnum;
import com.enation.app.javashop.model.shop.vo.ShopChangeMsg;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 是否为自营变化处理
 *
 * @author zh
 * @version v7.0
 * @date 19/3/27 下午2:26
 * @since v7.0
 */
@Component
public class ShopSelfOperatedChanageConsumer implements ShopChangeEvent {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private ExchangeGoodsClient exchangeGoodsClient;

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void shopChange(ShopChangeMsg shopChangeMsg) {
        try {
            if (!StringUtil.equals(ShopMessageTypeEnum.All.value(), shopChangeMsg.getMessageType())) {
                return;
            }
            //原店铺数据
            ShopVO originalShop = shopChangeMsg.getOriginalShop();
            //更新后店铺数据
            ShopVO shop = shopChangeMsg.getNewShop();
            //如果原店铺是自营改变为普通商家则进行处理
            if (originalShop.getSelfOperated().equals(1) && shop.getSelfOperated().equals(0)) {
                //查询此店铺的所有积分商品
                List<GoodsDO> goods = goodsClient.listPointGoods(shop.getShopId());
                for (GoodsDO goodsDO : goods) {
                    ExchangeDO exchangeDO = exchangeGoodsClient.getModelByGoods(goodsDO.getGoodsId());
                    if (exchangeDO != null) {
                        //删除此店铺的所有积分商品
                        exchangeGoodsClient.del(goodsDO.getGoodsId());
                        //删除此店铺的所有积分活动
                        promotionGoodsClient.delPromotionGoods(goodsDO.getGoodsId(), PromotionTypeEnum.EXCHANGE.name(), exchangeDO.getExchangeId());
                    }
                }
                //更改所有的商品为普通商品
                goodsClient.updateGoodsType(shop.getShopId(), GoodsType.NORMAL.name());
            }
        } catch (Exception e) {
            logger.error("处理店铺类型改变出错" + e.getMessage());
        }
    }
}
