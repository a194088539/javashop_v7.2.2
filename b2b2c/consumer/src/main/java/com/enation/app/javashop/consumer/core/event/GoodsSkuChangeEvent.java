package com.enation.app.javashop.consumer.core.event;

import java.util.List;

/**
 * 商品sku变化事件
 * @author fk
 * @version v2.0
 * @since v7.2.0
 * 2020年2月7日 上午10:24:40
 */
public interface GoodsSkuChangeEvent {

	/**
	 * 商品变化后需要执行的方法
     * @param delSkuIds
     */
	void goodsSkuChange(List<Long> delSkuIds);
}
