package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.trade.order.dos.TransactionRecord;

import java.util.List;

/**
 * 交易记录表业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-25 15:37:56
 */
public interface TransactionRecordManager	{

	/**
	 * 查询交易记录表列表
	 * @param orderSn 页码
	 * @return List
	 */
	List<TransactionRecord> listAll(String orderSn);

	/**
	 * 添加交易记录表
	 * @param transactionRecord 交易记录表
	 * @return TransactionRecord 交易记录表
	 */
	TransactionRecord add(TransactionRecord transactionRecord);

	/**
	* 修改交易记录表
	* @param transactionRecord 交易记录表
	* @param id 交易记录表主键
	* @return TransactionRecord 交易记录表
	*/
	TransactionRecord edit(TransactionRecord transactionRecord, Long id);

	/**
	 * 删除交易记录表
	 * @param id 交易记录表主键
	 */
	void delete(Long id);

	/**
	 * 获取交易记录表
	 * @param id 交易记录表主键
	 * @return TransactionRecord  交易记录表
	 */
	TransactionRecord getModel(Long id);

}
