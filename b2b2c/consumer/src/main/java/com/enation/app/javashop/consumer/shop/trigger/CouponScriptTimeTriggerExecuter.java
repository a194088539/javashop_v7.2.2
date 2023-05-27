package com.enation.app.javashop.consumer.shop.trigger;

import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.message.PromotionScriptMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.tool.enums.ScriptOperationTypeEnum;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.trigger.Interface.TimeTriggerExecuter;
import com.enation.app.javashop.framework.util.ScriptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 优惠券脚本生成和删除延时任务执行器
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.0
 * 2020-02-14
 */
@Component("couponScriptTimeTriggerExecuter")
public class CouponScriptTimeTriggerExecuter implements TimeTriggerExecuter {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private CouponClient couponClient;

    @Autowired
    private Cache cache;

    @Override
    public void execute(Object object) {

        PromotionScriptMsg promotionScriptMsg = (PromotionScriptMsg) object;

        //获取促销活动ID
        Long promotionId = promotionScriptMsg.getPromotionId();

        //优惠券级别缓存key
        String cacheKey = CachePrefix.COUPON_PROMOTION.getPrefix() + promotionId;

        //如果是优惠券开始生效
        if (ScriptOperationTypeEnum.CREATE.equals(promotionScriptMsg.getOperationType())) {

            //获取优惠券详情
            CouponDO coupon = this.couponClient.getModel(promotionId);

            //渲染并读取优惠券脚本信息
            String script = renderCouponScript(coupon);

            //将优惠券脚本信息放入缓存
            cache.put(cacheKey, script);

            //优惠券生效后，立马设置一个优惠券失效的流程
            promotionScriptMsg.setOperationType(ScriptOperationTypeEnum.DELETE);
            String uniqueKey = "{TIME_TRIGGER_" + promotionScriptMsg.getPromotionType().name() + "}_" + promotionId;
            timeTrigger.add(TimeExecute.COUPON_SCRIPT_EXECUTER, promotionScriptMsg, promotionScriptMsg.getEndTime(), uniqueKey);

            this.logger.debug("优惠券[" + promotionScriptMsg.getPromotionName() + "]开始生效，id=[" + promotionId + "]");
        } else {

            //删除缓存中的促销脚本数据
            cache.remove(cacheKey);

            this.logger.debug("优惠券[" + promotionScriptMsg.getPromotionName() + "]已经失效，id=[" + promotionId + "]");
        }
    }

    /**
     * 渲染并读取优惠券脚本信息
     * @param coupon 优惠券信息
     * @return
     */
    private String renderCouponScript(CouponDO coupon) {
        Map<String, Object> model = new HashMap<>();

        Map<String, Object> params = new HashMap<>();
        params.put("startTime", coupon.getStartTime().toString());
        params.put("endTime", coupon.getEndTime().toString());
        params.put("couponPrice", coupon.getCouponPrice());

        model.put("coupon", params);

        String path = "coupon.ftl";
        String script = ScriptUtil.renderScript(path, model);

        logger.debug("生成优惠券脚本：" + script);

        return script;
    }
}
