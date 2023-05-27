package com.enation.app.javashop.client.promotion.impl;

import com.enation.app.javashop.client.promotion.PromotionScriptClient;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.service.promotion.groupbuy.GroupbuyScriptManager;
import com.enation.app.javashop.service.promotion.seckill.SeckillScriptManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 促销脚本客户端
 *
 * @author fk
 * @version v7.0
 * @date 19/3/28 上午11:10
 * @since v7.0
 */
@Service
public class PromotionScriptClientImpl implements PromotionScriptClient {

    @Autowired
    private GroupbuyScriptManager groupbuyScriptManager;

    @Autowired
    private SeckillScriptManager seckillScriptManager;

    @Override
    public void createGroupBuyCacheScript(Long promotionId, List<PromotionGoodsDO> goodsList) {
        groupbuyScriptManager.createCacheScript(promotionId, goodsList);
    }

    @Override
    public void deleteGroupBuyCacheScript(Long promotionId, List<PromotionGoodsDO> goodsList) {
        groupbuyScriptManager.deleteCacheScript(promotionId, goodsList);
    }

    @Override
    public void deleteCacheScript(Long promotionId, List<SeckillApplyDO> goodsList) {
        seckillScriptManager.deleteCacheScript(promotionId, goodsList);
    }
}
