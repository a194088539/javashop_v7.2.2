package com.enation.app.javashop.consumer.job.handler;

import com.enation.app.javashop.model.base.JobAmqpExchange;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 每日执行
 *
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-06 上午4:24
 */
@JobHandler(value = "everyYearExecuteJobHandler")
@Component
public class EveryYearExecuteJobHandler extends IJobHandler {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MessageSender messageSender;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        try {
            messageSender.send(new MqMessage(JobAmqpExchange.EVERY_YEAR_EXECUTE,
                    JobAmqpExchange.EVERY_YEAR_EXECUTE + "_ROUTING",
                    ""));
        } catch (Exception e) {
            this.logger.error("每年任务AMQP消息发送异常：", e);
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }
}
