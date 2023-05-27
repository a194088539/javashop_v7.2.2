package com.enation.app.javashop.service.promotion.groupbuy;

import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;

import java.util.List;

/**
 * 团购促销活动脚本业务接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-18
 */
public interface GroupbuyScriptManager {

    /**
     * 创建参与团购促销活动商品的脚本数据信息
     * @param promotionId 团购促销活动ID
     * @param goodsList 参与团购促销活动的商品集合
     */
    void createCacheScript(Long promotionId, List<PromotionGoodsDO> goodsList);

    /**
     * 删除商品存放在缓存中的团购促销活动相关的脚本数据信息
     * @param promotionId 团购促销活动ID
     * @param goodsList 参与团购促销活动的商品集合
     */
    void deleteCacheScript(Long promotionId, List<PromotionGoodsDO> goodsList);
}
