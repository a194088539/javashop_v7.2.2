package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.GoodsWordsChangeEvent;
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

import java.util.List;

/**
* @author liuyulei
 * @version 1.0
 * @Description: 提示词变化消息接收者
 * @date 2019/6/13 17:15
 * @since v7.0
 */
@Component
public class GoodsWordsChangeReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<GoodsWordsChangeEvent> events;

	/**
	 * 提示词变化
	 * 
	 * @param words
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.GOODS_WORDS_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.GOODS_WORDS_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void goodsChange(String words) {

		if (events != null) {
			for (GoodsWordsChangeEvent event : events) {
				try {
					event.wordsChange(words);
				} catch (Exception e) {
					logger.error("处理提示词变化消息出错", e);
				}
			}
		}

	}
}
