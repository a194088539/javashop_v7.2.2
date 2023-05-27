package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.GoodsSkuChangeEvent;
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
 * 商品sku变化消费者
 *
 * @author fk
 * @version v2.0
 * @since v7.2.0 2020年2月7日 上午10:29:54
 */
@Component
public class GoodsSkuChangeReceiver {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired(required = false)
	private List<GoodsSkuChangeEvent> events;

	/**
	 * 商品变化
	 *
	 * @param delSkuIds
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.GOODS_SKU_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.GOODS_SKU_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void goodsSkuChange(List<Long> delSkuIds) {

		if (events != null) {
			for (GoodsSkuChangeEvent event : events) {
				try {
					event.goodsSkuChange(delSkuIds);
				} catch (Exception e) {
					logger.error("处理商品sku变化消息出错", e);
				}
			}
		}

	}
}
