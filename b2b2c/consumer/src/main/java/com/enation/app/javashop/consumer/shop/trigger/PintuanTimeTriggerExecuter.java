package com.enation.app.javashop.consumer.shop.trigger;

import com.enation.app.javashop.client.trade.PintuanClient;
import com.enation.app.javashop.model.base.message.PintuanChangeMsg;
import com.enation.app.javashop.model.base.rabbitmq.TimeExecute;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOptionEnum;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionStatusEnum;
import com.enation.app.javashop.framework.trigger.Interface.TimeTrigger;
import com.enation.app.javashop.framework.trigger.Interface.TimeTriggerExecuter;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 拼团定时开启关闭活动 延时任务执行器
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2019-02-13 下午5:34
 */
@Component("pintuanTimeTriggerExecute")
public class PintuanTimeTriggerExecuter implements TimeTriggerExecuter {

    @Autowired
    private TimeTrigger timeTrigger;

    @Autowired
    private PintuanClient pintuanClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 执行任务
     *
     * @param object 任务参数
     */
    @Override
    public void execute(Object object) {
        PintuanChangeMsg pintuanChangeMsg = (PintuanChangeMsg) object;

        //如果是要开启活动

        if (pintuanChangeMsg.getOptionType() == 1) {
            Pintuan pintuan = pintuanClient.getModel(pintuanChangeMsg.getPintuanId());
            if (PromotionStatusEnum.WAIT.name().equals(pintuan.getStatus()) ||
                    (PromotionStatusEnum.END.name().equals(pintuan.getStatus()) && PintuanOptionEnum.CAN_OPEN.name().equals(pintuan.getOptionStatus()))) {
                pintuanClient.openPromotion(pintuanChangeMsg.getPintuanId());
                //开启活动后，立马设置一个关闭的流程
                pintuanChangeMsg.setOptionType(0);
                timeTrigger.add(TimeExecute.PINTUAN_EXECUTER, pintuanChangeMsg, pintuan.getEndTime(), "{TIME_TRIGGER}_" + pintuan.getPromotionId());
                this.logger.debug("活动[" + pintuan.getPromotionName() + "]开始,id=[" + pintuan.getPromotionId() + "]");
            }
        } else {
            //拼团活动结束
            Pintuan pintuan = pintuanClient.getModel(pintuanChangeMsg.getPintuanId());
            if (pintuan.getStatus().equals(PromotionStatusEnum.UNDERWAY.name())) {
                pintuanClient.closePromotion(pintuanChangeMsg.getPintuanId());
            }
            this.logger.debug("活动[" + pintuan.getPromotionName() + "]结束,id=[" + pintuan.getPromotionId() + "]");
        }
    }
}
