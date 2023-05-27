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

import com.enation.app.javashop.consumer.core.event.GoodsCommentEvent;
import com.enation.app.javashop.model.base.message.GoodsCommentMsg;

/**
 * 商品评论
 * 
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:30:02
 */
@Component
public class GoodsCommentReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<GoodsCommentEvent> events;

	/**
	 * 商品评论
	 * 
	 * @param goodsCommentMsg
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.GOODS_COMMENT_COMPLETE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.GOODS_COMMENT_COMPLETE, type = ExchangeTypes.FANOUT)
	))
	public void commentComplete(GoodsCommentMsg goodsCommentMsg) {

		if (events != null) {
			for (GoodsCommentEvent event : events) {
				try {
					event.goodsComment(goodsCommentMsg);
				} catch (Exception e) {
					logger.error("处理商品评论出错",e);
				}
			}
		}

	}
}
