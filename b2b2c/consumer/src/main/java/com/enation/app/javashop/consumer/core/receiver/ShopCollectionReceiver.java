package com.enation.app.javashop.consumer.core.receiver;

import java.util.List;

import com.enation.app.javashop.consumer.core.event.ShopCollectionEvent;
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


/**
 * 店铺变更消费者
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018年3月23日 上午10:32:08
 */
@Component
public class ShopCollectionReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required=false)
	private List<ShopCollectionEvent> events;
	/**
	 * 店铺变更
	 *
	 * @param shopId
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.SHOP_CHANGE_REGISTER + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.SHOP_CHANGE_REGISTER, type = ExchangeTypes.FANOUT)
	))
	public void shopChange(Long shopId) {

		if (events != null) {
			for (ShopCollectionEvent event : events) {
				try {
					event.shopChange(shopId);
				} catch (Exception e) {
					logger.error("店铺变更消息出错", e);
				}
			}
		}

	}

}
