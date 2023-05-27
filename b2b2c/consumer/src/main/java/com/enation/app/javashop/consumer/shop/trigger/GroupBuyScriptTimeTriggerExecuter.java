package com.enation.app.javashop.consumer.shop.trigger;

import com.enation.app.javashop.client.promotion.PromotionGoodsClient;
import com.enation.app.javashop.client.promotion.PromotionScriptClient;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.trigger.Interface.TimeTriggerExecuter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 团购促销活动脚本生成和删除延时任务执行器
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-18
 */
@Component("groupBuyScriptTimeTriggerExecuter")
public class GroupBuyScriptTimeTriggerExecuter implements TimeTriggerExecuter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private PromotionScriptClient promotionScriptClient;

    @Autowired
    private PromotionGoodsClient promotionGoodsClient;

    @Override
    public void execute(Object object) {

        PromotionScriptMsg promotionScriptMsg = (PromotionScriptMsg) object;

        //获取促销活动ID
        Long promotionId = promotionScriptMsg.getPromotionId();

        //获取参与团购活动的所有商品信息集合
        List<PromotionGoodsDO> goodsList = this.promotionGoodsClient.getPromotionGoods(promotionId, PromotionTypeEnum.GROUPBUY.name());

        //如果是团购促销活动开始生效
        if (ScriptOperationTypeEnum.CREATE.equals(promotionScriptMsg.getOperationType())) {
            //创建脚本信息
            this.promotionScriptClient.createGroupBuyCacheScript(promotionId, goodsList);

            //开启活动后，立马设置一个关闭的流程
            promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.DELETE);

            String uniqueKey = "{TIME_TRIGGER_" + promotionScriptMsg.getPromotionType().name() + "}_" + promotionId;

            timeTrigger.add(TimeExecute.GROUPBUY_SCRIPT_EXECUTER, promotionScriptMsg, promotionScriptMsg.getEndTime(), uniqueKey);

            this.logger.debug("促销活动[" + promotionScriptMsg.getPromotionName() + "]开始，id=[" + promotionId + "]");

        } else {
            //删除脚本信息
            this.promotionScriptClient.deleteGroupBuyCacheScript(promotionId, goodsList);

            this.logger.debug("促销活动[" + promotionScriptMsg.getPromotionName() + "]结束，id=[" + promotionId + "]");
        }

    }
}
