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

import com.enation.app.javashop.consumer.core.event.CategoryChangeEvent;
import com.enation.app.javashop.model.base.message.CategoryChangeMsg;

/**
 * 分类 变更
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018年3月23日 上午10:29:42
 */
@Component
public class CategoryChangeReceiver {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required=false)
	private List<CategoryChangeEvent> events;



	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.GOODS_CATEGORY_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.GOODS_CATEGORY_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void categoryChange(CategoryChangeMsg categoryChangeMsg){
		
		if(events!=null){
			for(CategoryChangeEvent event : events){
				try {
					event.categoryChange(categoryChangeMsg);
				} catch (Exception e) {
					logger.error("处理商品分类变化消息出错",e);
				}
			}
		}
		
	}
}
