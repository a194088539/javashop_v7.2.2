package com.enation.app.javashop.service.trade.deposite;

import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.trade.deposite.RechargeDO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 充值记录业务层
 * @author admin
 * @version v7.1.5
 * @since v7.1.5
 * 2019-12-30 16:38:45
 */
public interface RechargeManager {


	/**
	 * 创建充值订单
	 * @param money 充值金额
	 */
	RechargeDO recharge(Double money);

	/**
	 * 支付成功
	 * @param sn  充值订单编号
	 * @param price  充值金额
	 */
	void paySuccess(String sn,Double price);


	/**
	 * 修改支付方式
	 * @param subSn
	 * @param pluginId
	 * @param methodName
	 */
	void updatePaymentMethod(String subSn, String pluginId, String methodName);


	/**
	 * 查询充值记录列表
	 * @param paramDTO 搜索参数
	 * @return WebPage
	 */
	WebPage list(DepositeParamDTO paramDTO);

	/**
	 * 根据充值订单获取充值金额
	 *
	 * @param sn  充值订单编号
	 * @return
	 */
	Double getPrice(String sn);


	
	/**
	 * 获取充值记录
	 * @param sn 充值订单编号
	 * @return DepositeRecharge  充值记录
	 */
	RechargeDO getModel(String sn);

}