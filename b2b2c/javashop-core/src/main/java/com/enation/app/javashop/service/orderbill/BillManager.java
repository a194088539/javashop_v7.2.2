package com.enation.app.javashop.service.orderbill;

import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.orderbill.vo.BillDetail;
import com.enation.app.javashop.model.orderbill.vo.BillExcel;
import com.enation.app.javashop.model.orderbill.vo.BillQueryParam;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.orderbill.dos.Bill;

/**
 * 结算单业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-04-26 16:21:26
 */
public interface BillManager	{

	/**
	 * 查询结算单列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);
	/**
	 * 添加结算单
	 * @param bill 结算单
	 * @return Bill 结算单
	 */
	Bill add(Bill bill);

	/**
	 * 获取结算单
	 * @param id 结算单主键
	 * @return Bill  结算单
	 */
	Bill getModel(Long id);

	/**
	 * 生成结算单
	 * @param startTime
	 * @param endTime
	 */
	void createBills(Long startTime,Long endTime);

	/**
	 * 查询账单列表
	 * @param param
	 * @return
	 */
    WebPage queryBills(BillQueryParam param);

	/**
	 * 修改账单的状态
	 * @param billId
	 * @param permission
	 * @return
	 */
	Bill editStatus(Long billId, Permission permission);

	/**
	 * 获取结算单详细
	 * @param billId
	 * @param permission
	 * @return
	 */
	BillDetail getBillDetail(Long billId,Permission permission);

	/**
	 * 查看每个周期的结果统计
	 * @param pageNo
	 * @param pageSize
	 * @param sn
	 * @return
	 */
	WebPage getAllBill(Long pageNo, Long pageSize, String sn);

	/**
	 * 结算单导出
	 * @param billId
	 * @return
	 */
	BillExcel exportBill(Long billId);
}
