package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.trade.order.dos.OrderOutStatus;
import com.enation.app.javashop.model.trade.order.enums.OrderOutStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderOutTypeEnum;

/**
 * 订单出库状态业务层
 * @author xlp
 * @version v2.0
 * @since v7.0.0
 * 2018-07-10 14:06:38
 */
public interface OrderOutStatusManager	{

	/**
	 * 查询订单出库状态列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);

	/**
	 * 添加订单出库状态
	 * @param orderOutStatus 订单出库状态
	 * @return OrderOutStatus 订单出库状态
	 */
	OrderOutStatus add(OrderOutStatus orderOutStatus);

	/**
	* 修改订单出库状态
	* @param orderSn 订单编号
	* @param typeEnum 出库类型
	* @param  statusEnum  出库状态
	* @return OrderOutStatus 订单出库状态
	*/
	void edit(String orderSn, OrderOutTypeEnum typeEnum, OrderOutStatusEnum statusEnum);

	/**
	 * 删除订单出库状态
	 * @param id 订单出库状态主键
	 */
	void delete(Long id);

	/**
	 * 获取订单出库状态
	 * @param orderSn	订单编号
	 * @param typeEnum	出库类型
	 * @return OrderOutStatus  订单出库状态
	 */
	OrderOutStatus getModel(String orderSn, OrderOutTypeEnum typeEnum);

}
