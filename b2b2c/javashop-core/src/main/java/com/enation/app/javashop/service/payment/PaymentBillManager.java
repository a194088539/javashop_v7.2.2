package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 支付帐单业务层
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-04-16 17:28:07
 */
public interface PaymentBillManager	{

	/**
	 * 查询支付帐单列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);
	/**
	 * 添加支付帐单
	 * @param paymentBill 支付帐单
	 * @return PayBill 支付帐单
	 */
	PayBill add(PaymentBillDO paymentBill);

	/**
	 * 使用第三方单号查询流水
	 * @param returnTradeNo
	 * @return
	 */
	PaymentBillDO getBillByReturnTradeNo(String returnTradeNo);

	/**
	 * 根据账单编号获取详细
	 * @param billSn  账单编号
	 * @return
	 */
	PaymentBillDO getModel(String billSn);


	/**
	 * 修改账单支付方式
	 * @param paymentBillDO
	 */
	void edit(PaymentBillDO paymentBillDO,String sn );

	/**
	 * 检测账单是否可以被支付
	 * @param billSn
	 * @return  true 可以支付   false 不能支付
	 */
	boolean check(String billSn);
	/**
	 * 支付成功调用
	 * @param billSn 支付账单号
	 * @param returnTradeNo 第三方平台回传单号（第三方平台的支付单号）
	 * @param payPrice 支付金额
	 */
	void paySuccess(String billSn,String returnTradeNo,Double payPrice);


	/**
	 * 根据子业务编号和子业务类型获取账单信息
	 *
	 * @param subSn
	 * @param serviceType
	 * @return
	 */
	PaymentBillDO getBySubSnAndServiceType(String subSn, String serviceType);


}