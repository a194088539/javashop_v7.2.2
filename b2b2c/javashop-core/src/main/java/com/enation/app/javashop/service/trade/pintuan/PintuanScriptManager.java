package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDO;

import java.util.List;

/**
 * 拼团促销活动脚本业务接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2020-02-17
 */
public interface PintuanScriptManager {

    /**
     * 创建参与拼团促销活动商品的脚本数据信息
     * @param promotionId 拼团促销活动ID
     * @param goodsList 参与拼团促销活动的商品集合
     */
    void createCacheScript(Long promotionId, List<PintuanGoodsDO> goodsList);

    /**
     * 删除商品存放在缓存中的拼团促销活动相关的脚本数据信息
     * @param promotionId 拼团促销活动ID
     * @param goodsList 参与拼团促销活动的商品集合
     */
    void deleteCacheScript(Long promotionId, List<PintuanGoodsDO> goodsList);

}
