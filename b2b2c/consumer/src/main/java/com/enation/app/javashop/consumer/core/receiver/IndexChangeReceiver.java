package com.enation.app.javashop.consumer.core.receiver;

import java.util.List;

import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.javashop.consumer.core.event.IndexChangeEvent;
import com.enation.app.javashop.model.base.message.CmsManageMsg;

/**
 * 首页生成
 * 
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:30:51
 */
@Component
public class IndexChangeReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<IndexChangeEvent> events;

	/**
	 * 生成首页
	 * @param operation
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.PC_INDEX_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.PC_INDEX_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void createIndexPage(CmsManageMsg operation) {

		if (events != null) {
			for (IndexChangeEvent event : events) {
				try {
					event.createIndexPage(operation);
				} catch (Exception e) {
					logger.error("首页生成", e);
				}
			}
		}

	}

}
