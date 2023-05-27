package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.job.execute.EveryMonthExecute;
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
 * 每月执行调用
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-25 上午8:26
 */
@Component
public class EveryMonthExecuteReceiver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<EveryMonthExecute> everyMonthExecutes;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = JobAmqpExchange.EVERY_MONTH_EXECUTE + "_QUEUE"),
            exchange = @Exchange(value = JobAmqpExchange.EVERY_MONTH_EXECUTE, type = ExchangeTypes.FANOUT)
    ))
    public void everyMonth() {

        if(everyMonthExecutes != null){
            for (EveryMonthExecute everyMonthExecute : everyMonthExecutes) {
                try {
                    everyMonthExecute.everyMonth();
                } catch (Exception e) {
                    logger.error("每月任务异常：", e);
                }
            }
        }
    }


}
