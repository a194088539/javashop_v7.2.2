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

import com.enation.app.javashop.consumer.core.event.HelpChangeEvent;

/**
 * 帮助中心页面生成
 * 
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:30:38
 */
@Component
public class HelpChangeReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<HelpChangeEvent> events;

	/**
	 * 消费执行者
	 * 
	 * @param articeids
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.HELP_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.HELP_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void helpChange(List<Integer> articeids) {

		if (events != null) {
			for (HelpChangeEvent event : events) {
				try {
					event.helpChange(articeids);
				} catch (Exception e) {
					logger.error("生成帮助中心页面出错", e);
				}
			}
		}

	}
}
