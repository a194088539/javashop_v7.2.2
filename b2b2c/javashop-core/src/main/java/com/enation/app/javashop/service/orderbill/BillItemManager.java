package com.enation.app.javashop.service.orderbill;

import com.enation.app.javashop.model.orderbill.vo.BillResult;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.orderbill.dos.BillItem;

import java.util.Map;

/**
 * 结算单项表业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-04-26 15:39:57
 */
public interface BillItemManager	{

	/**
	 * 查询结算单项表列表
	 * @param page
	 * @param pageSize
	 * @param billId
	 * @param billType
	 * @return
	 */
	WebPage list(long page, long pageSize, Long billId, String billType);
	/**
	 * 添加结算单项表
	 * @param billItem 结算单项表
	 * @return BillItem 结算单项表
	 */
	BillItem add(BillItem billItem);

	/**
	* 修改结算单项表
	* @param billItem 结算单项表
	* @param id 结算单项表主键
	* @return BillItem 结算单项表
	*/
	BillItem edit(BillItem billItem, Long id);

	/**
	 * 删除结算单项表
	 * @param id 结算单项表主键
	 */
	void delete(Long id);

	/**
	 * 获取结算单项表
	 * @param id 结算单项表主键
	 * @return BillItem  结算单项表
	 */
	BillItem getModel(Long id);

	/**
	 * 更新结算项的状态
	 * @param sellerId
	 * @param billId
	 * @param startTime
	 * @param lastTime
	 */
    void updateBillItem(Long sellerId, Long billId, String startTime, String lastTime);

	/**
	 * 查询结算单项的统计结果
	 * @param startTime
	 * @param lastTime
	 * @return key是卖家id
	 */
	Map<Long, BillResult> countBillResultMap(String startTime, String lastTime);
}
