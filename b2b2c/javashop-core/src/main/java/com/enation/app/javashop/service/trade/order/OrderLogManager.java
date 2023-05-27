package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.trade.order.dos.OrderLogDO;

import java.util.List;

/**
 * 订单日志表业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-16 12:01:34
 */
public interface OrderLogManager {

	/**
	 * 查询订单日志表列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);


	/**
	 * 查询订单日志表列表
	 * @param orderSn 订单编号
	 * @return List
	 */
	List listAll(String orderSn);


	/**
	 * 添加订单日志表
	 * @param orderLog 订单日志表
	 * @return OrderLog 订单日志表
	 */
	OrderLogDO add(OrderLogDO orderLog);

	/**
	* 修改订单日志表
	* @param orderLog 订单日志表
	* @param id 订单日志表主键
	* @return OrderLog 订单日志表
	*/
	OrderLogDO edit(OrderLogDO orderLog, Long id);

	/**
	 * 删除订单日志表
	 * @param id 订单日志表主键
	 */
	void delete(Long id);

	/**
	 * 获取订单日志表
	 * @param id 订单日志表主键
	 * @return OrderLog  订单日志表
	 */
	OrderLogDO getModel(Long id);

}
