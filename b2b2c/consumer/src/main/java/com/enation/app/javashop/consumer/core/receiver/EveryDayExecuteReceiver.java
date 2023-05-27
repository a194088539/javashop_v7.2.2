package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.job.execute.EveryDayExecute;
import com.enation.app.javashop.model.base.JobAmqpExchange;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 每日执行调用
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-25 上午8:26
 */
@Component
public class EveryDayExecuteReceiver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<EveryDayExecute> everyDayExecutes;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = JobAmqpExchange.EVERY_DAY_EXECUTE + "_QUEUE"),
            exchange = @Exchange(value = JobAmqpExchange.EVERY_DAY_EXECUTE, type = ExchangeTypes.FANOUT)
    ))
    public void everyDay() {

        if (everyDayExecutes != null) {
            for (EveryDayExecute everyDayExecute : everyDayExecutes) {
                try {
                    everyDayExecute.everyDay();
                } catch (Exception e) {
                    logger.error("每日任务异常：", e);
                }
            }
        }
    }

}
