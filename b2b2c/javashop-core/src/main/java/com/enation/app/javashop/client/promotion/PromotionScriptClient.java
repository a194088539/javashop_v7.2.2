package com.enation.app.javashop.client.promotion;

import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;

import java.util.List;

/**
 * 促销脚本客户端
 *
 * @author fk
 * @version v7.0
 * @date 19/3/28 上午11:10
 * @since v7.0
 */
public interface PromotionScriptClient {

    /**
     * 创建参与团购促销活动商品的脚本数据信息
     * @param promotionId 团购促销活动ID
     * @param goodsList 参与团购促销活动的商品集合
     */
    void createGroupBuyCacheScript(Long promotionId, List<PromotionGoodsDO> goodsList);

    /**
     * 删除商品存放在缓存中的团购促销活动相关的脚本数据信息
     * @param promotionId 团购促销活动ID
     * @param goodsList 参与团购促销活动的商品集合
     */
    void deleteGroupBuyCacheScript(Long promotionId, List<PromotionGoodsDO> goodsList);

    /**
     * 删除商品存放在缓存中的限时抢购促销活动相关的脚本数据信息
     * @param promotionId 限时抢购促销活动ID
     * @param goodsList 参与限时抢购促销活动的商品集合
     */
    void deleteCacheScript(Long promotionId, List<SeckillApplyDO> goodsList);


}
