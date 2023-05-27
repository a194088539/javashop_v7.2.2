package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.PaymentBillChangeEvent;
import com.enation.app.javashop.model.base.message.PaymentBillStatusChangeMsg;
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
 * 支付账单状态改变消费者
 * 
 * @author fk
 * @version v2.0
 * @since v7.2.1 2020年3月11日 上午10:31:42
 */
@Component
public class PaymentBillChangeReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired(required = false)
	private List<PaymentBillChangeEvent> events;

	/**
	 * 订单状态改变
	 * @param paymentBillMessage
	 */
	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = AmqpExchange.PAYMENT_BILL_CHANGE + "_QUEUE"),
			exchange = @Exchange(value = AmqpExchange.PAYMENT_BILL_CHANGE, type = ExchangeTypes.FANOUT)
	))
	public void billChange(PaymentBillStatusChangeMsg paymentBillMessage) {
		if (events != null) {
			for (PaymentBillChangeEvent event : events) {
				try {
					event.billChange(paymentBillMessage);
				} catch (Exception e) {
					logger.error("支付账单状态改变消息出错", e);
				}
			}
		}

	}

}
