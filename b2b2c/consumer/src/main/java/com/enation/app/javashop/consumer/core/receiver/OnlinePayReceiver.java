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

import com.enation.app.javashop.consumer.core.event.OnlinePayEvent;

/**
 * 在线支付
 * 
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:31:35
 */
@Component
public class OnlinePayReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<OnlinePayEvent> events;

	/**
	 * 在线支付
	 * 
	 * @param memberId
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.ONLINE_PAY + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.ONLINE_PAY, type = ExchangeTypes.FANOUT)
	))
	public void onlinePay(Long memberId) {
		if (events != null) {
			for (OnlinePayEvent event : events) {
				try {
					event.onlinePay(memberId);
				} catch (Exception e) {
					logger.error("在线支付出错", e);
				}
			}
		}

	}
}
