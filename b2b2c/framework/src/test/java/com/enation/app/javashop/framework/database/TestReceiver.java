package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 *  用于
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018年3月23日 上午10:29:42
 */
@Component
public class TestReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());


	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = RabbitMqTest.KEY+ "_QUEUE"),
			exchange = @Exchange(value =  RabbitMqTest.KEY, type = ExchangeTypes.FANOUT)
	))
	public void categoryChange(String msg){

		System.out.println("revice : "+ msg);

	}
}
