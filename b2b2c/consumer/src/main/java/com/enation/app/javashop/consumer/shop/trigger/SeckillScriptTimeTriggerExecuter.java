package com.enation.app.javashop.consumer.shop.trigger;

import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.client.promotion.PromotionScriptClient;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.enums.SeckillGoodsApplyStatusEnum;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.trigger.Interface.TimeTriggerExecuter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 限时抢购促销活动脚本删除延时任务执行器
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-19
 */
@Component("seckillScriptTimeTriggerExecuter")
public class SeckillScriptTimeTriggerExecuter implements TimeTriggerExecuter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PromotionScriptClient promotionScriptClient;

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;

    @Override
    public void execute(Object object) {

        PromotionScriptMsg promotionScriptMsg = (PromotionScriptMsg) object;

        //如果是限时抢购促销活动结束
        if (ScriptOperationTypeEnum.DELETE.equals(promotionScriptMsg.getOperationType())) {

            //获取促销活动ID
            Long promotionId = promotionScriptMsg.getPromotionId();

            //获取所有参与活动审核通过的商品集合
            List<SeckillApplyDO> goodsList = this.promotionGoodsClient.getSeckillGoodsList(promotionId, SeckillGoodsApplyStatusEnum.PASS.value());

            //清除促销活动脚本信息
            this.promotionScriptClient.deleteCacheScript(promotionId, goodsList);

            this.logger.debug("促销活动[" + promotionScriptMsg.getPromotionName() + "]结束，id=[" + promotionId + "]");
        }

    }
}
