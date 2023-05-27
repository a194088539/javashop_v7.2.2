package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.job.execute.EveryYearExecute;
import com.enation.app.javashop.model.base.JobAmqpExchange;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 每年执行调用
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-25 上午8:26
 */
public class EveryYearExecuteReceiver {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private List<EveryYearExecute> everyYearExecutes;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = JobAmqpExchange.EVERY_YEAR_EXECUTE + "_QUEUE"),
            exchange = @Exchange(value = JobAmqpExchange.EVERY_YEAR_EXECUTE, type = ExchangeTypes.FANOUT)
    ))
    public void everyYear() {

        if (everyYearExecutes != null) {
            for (EveryYearExecute everyYearExecute : everyYearExecutes) {
                try {
                    everyYearExecute.everyYear();
                } catch (Exception e) {
                    logger.error("每年任务异常：", e);
                }

            }
        }


    }


}
