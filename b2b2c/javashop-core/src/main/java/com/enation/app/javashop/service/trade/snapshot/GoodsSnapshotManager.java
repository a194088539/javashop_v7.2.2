package com.enation.app.javashop.service.trade.snapshot;

import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.snapshot.SnapshotVO;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.trade.snapshot.GoodsSnapshot;

/**
 * 交易快照业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-01 14:55:26
 */
public interface GoodsSnapshotManager	{

	/**
	 * 查询交易快照列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);
	/**
	 * 添加交易快照
	 * @param goodsSnapshot 交易快照
	 * @return GoodsSnapshot 交易快照
	 */
	GoodsSnapshot add(GoodsSnapshot goodsSnapshot);

	/**
	* 修改交易快照
	* @param goodsSnapshot 交易快照
	* @param id 交易快照主键
	* @return GoodsSnapshot 交易快照
	*/
	GoodsSnapshot edit(GoodsSnapshot goodsSnapshot, Long id);

	/**
	 * 删除交易快照
	 * @param id 交易快照主键
	 */
	void delete(Long id);

	/**
	 * 获取交易快照
	 * @param id 交易快照主键
	 * @return GoodsSnapshot  交易快照
	 */
	GoodsSnapshot getModel(Long id);

	/**
	 * 添加交易快照
	 * @param orderDO
	 */
	void add(OrderDO orderDO);

	/**
	 * 查询快照VO
	 * @param id
	 * @param owner
     * @return
	 */
    SnapshotVO get(Long id, String owner);
}
