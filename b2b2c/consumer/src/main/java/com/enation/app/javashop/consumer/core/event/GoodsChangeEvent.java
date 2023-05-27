package com.enation.app.javashop.consumer.core.event;

import com.enation.app.javashop.model.base.message.GoodsChangeMsg;

/**
 * 商品变化事件
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018年3月23日 上午10:24:40
 */
public interface GoodsChangeEvent {

	/**
	 * 商品变化后需要执行的方法
	 * @param goodsChangeMsg
	 */
	void goodsChange(GoodsChangeMsg goodsChangeMsg);
}
