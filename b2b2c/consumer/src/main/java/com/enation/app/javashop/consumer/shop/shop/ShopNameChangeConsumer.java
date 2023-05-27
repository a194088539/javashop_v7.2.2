package com.enation.app.javashop.consumer.shop.shop;

import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.goods.GoodsIndexClient;
import com.enation.app.javashop.client.member.MemberCollectionShopClient;
import com.enation.app.javashop.client.member.MemberCouponClient;
import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.ShopChangeEvent;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.model.shop.enums.ShopMessageTypeEnum;
import com.enation.app.javashop.model.shop.vo.ShopChangeMsg;
import com.enation.app.javashop.model.shop.vo.ShopVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 店铺信息同步
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-12-10 下午4:05
 */
@Component
public class ShopNameChangeConsumer implements ShopChangeEvent {
    @Autowired
    private GoodsIndexClient goodsIndexClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private CouponClient couponClient;
    @Autowired
    private AfterSaleClient afterSaleClient;
    @Autowired
    private OrderOperateClient orderOperateClient;
    @Autowired
    private MemberCollectionShopClient memberCollectionShopClient;
    @Autowired
    private MemberCouponClient memberCouponClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 店铺名称改变消息
     *
     * @param shopChangeMsg 店铺名称改变消息
     */
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
            //如果店铺名称发生变化
            if (!originalShop.getShopName().equals(shop.getShopName())) {
                //修改商品的店铺名称
                goodsClient.changeSellerName(shop.getShopId(), shop.getShopName());
                //查询此店家的商品集合，循环更新索引
                List<Map<String, Object>> goods = goodsClient.getGoodsAndParams(shop.getShopId());
                if (goods.size() > 0) {
                    for (int i = 0; i < goods.size(); i++) {
                        goodsIndexClient.updateIndex(goods.get(i));
                    }
                }
                Long sellerId = shop.getShopId();
                String sellerName = shop.getShopName();
                //修改优惠券中的店铺名
                couponClient.editCouponShopName(sellerId, sellerName);
                //修改已领取的优惠券的店铺名称
                memberCouponClient.updateSellerName(sellerId, sellerName);
                //修改售后服务单的店铺名称
                afterSaleClient.editAfterSaleShopName(sellerId, sellerName);
                //修改订单的店铺名称
                orderOperateClient.editOrderShopName(sellerId, sellerName);
                //修改我的收藏的店铺名称
                memberCollectionShopClient.changeSellerName(sellerId, sellerName);

            }
        } catch (Exception e) {
            logger.error("处理店铺名称改变出错" + e.getMessage());
            e.printStackTrace();
        }
    }
}
