package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.model.payment.vo.PaymentMethodVO;
import com.enation.app.javashop.model.payment.vo.PaymentPluginVO;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;

import java.util.List;
import java.util.Map;

/**
 * 支付方式表业务层
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-04-11 16:06:57
 */
public interface PaymentMethodManager	{

	/**
	 * 查询支付方式表列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);
	/**
	 * 添加支付方式表
	 * @param paymentMethod 支付方式表
	 * @param paymentPluginId 插件id
	 * @return PaymentMethod 支付方式表
	 */
	PaymentMethodDO add(PaymentPluginVO paymentMethod,String paymentPluginId);

	/**
	* 修改支付方式表
	* @param paymentMethod 支付方式表
	* @param id 支付方式表主键
	* @return PaymentMethod 支付方式表
	*/
	PaymentMethodDO edit(PaymentMethodDO paymentMethod, Long id);

	/**
	 * 删除支付方式表
	 * @param id 支付方式表主键
	 */
	void delete(Long id);

	/**
	 * 根据支付插件id获取支付方式详细
	 * @param pluginId
	 * @return
	 */
	PaymentMethodDO getByPluginId(String pluginId);

	/**
	 * 根据插件id获取VO对象
	 * @param pluginId
	 * @return
	 */
	PaymentPluginVO getByPlugin(String pluginId);

	/**
	 * 查询某客户端支持的支付方式
	 * @param clientType
	 * @return
	 */
    List<PaymentMethodVO> queryMethodByClient(String clientType);


	/**
	 * 获取配置
	 * @param clientType
	 * @param paymentMethodId
	 * @return
	 */
	Map<String, String> getConfig(String clientType, String paymentMethodId);

}
