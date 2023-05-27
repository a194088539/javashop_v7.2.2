package com.enation.app.javashop.service.promotion.seckill;

import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;

import java.util.List;

/**
 * 限时抢购促销活动脚本业务接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-19
 */
public interface SeckillScriptManager {

    /**
     * 创建参与限时抢购促销活动商品的脚本数据信息
     * @param promotionId 限时抢购促销活动ID
     * @param goodsList 参与限时抢购促销活动的商品集合
     */
    void createCacheScript(Long promotionId, List<SeckillApplyDO> goodsList);

    /**
     * 删除商品存放在缓存中的限时抢购促销活动相关的脚本数据信息
     * @param promotionId 限时抢购促销活动ID
     * @param goodsList 参与限时抢购促销活动的商品集合
     */
    void deleteCacheScript(Long promotionId, List<SeckillApplyDO> goodsList);

}
