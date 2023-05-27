package com.enation.app.javashop.consumer.core.receiver;

import java.util.List;

import com.enation.app.javashop.consumer.core.event.GoodsChangeEvent;
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

import com.enation.app.javashop.model.base.message.GoodsChangeMsg;

/**
 * 商品变化消费者
 * 
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:29:54
 */
@Component
public class GoodsChangeReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<GoodsChangeEvent> events;

	/**
	 * 商品变化
	 * 
	 * @param goodsChangeMsg
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.GOODS_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.GOODS_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void goodsChange(GoodsChangeMsg goodsChangeMsg) {

		if (events != null) {
			for (GoodsChangeEvent event : events) {
				try {
					event.goodsChange(goodsChangeMsg);
				} catch (Exception e) {
					logger.error("处理商品变化消息出错", e);
				}
			}
		}

	}
}
