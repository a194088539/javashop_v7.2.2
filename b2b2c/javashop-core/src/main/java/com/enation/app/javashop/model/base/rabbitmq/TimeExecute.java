package com.enation.app.javashop.model.base.rabbitmq;

/**
 * 延迟加载任务执行器
 *
 * @author zh
 * @version v7.0
 * @date 19/3/1 下午2:13
 * @since v7.0
 */
public class TimeExecute {

    /**
     * 促销延迟加载执行器
     */
    public final static String PROMOTION_EXECUTER = "promotionTimeTriggerExecuter";
    /**
     * 拼团延迟加载执行器
     */
    public final static String PINTUAN_EXECUTER = "pintuanTimeTriggerExecute";
    /**
     * 商家促销活动脚本创建延迟加载执行器
     * 包括满减满赠、第二件半价、单品立减
     */
    public final static String SELLER_PROMOTION_SCRIPT_EXECUTER = "promotionScriptTimeTriggerExecuter";
    /**
     * 优惠券脚本创建延迟加载执行器
     */
    public final static String COUPON_SCRIPT_EXECUTER = "couponScriptTimeTriggerExecuter";
    /**
     * 团购促销活动脚本创建延迟加载执行器
     */
    public final static String GROUPBUY_SCRIPT_EXECUTER = "groupBuyScriptTimeTriggerExecuter";
    /**
     * 限时抢购促销活动脚本创建延迟加载执行器
     */
    public final static String SECKILL_SCRIPT_EXECUTER = "seckillScriptTimeTriggerExecuter";
}
