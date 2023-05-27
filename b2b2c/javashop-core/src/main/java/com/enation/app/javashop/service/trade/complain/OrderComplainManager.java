package com.enation.app.javashop.service.trade.complain;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.trade.complain.dto.ComplainDTO;
import com.enation.app.javashop.model.trade.complain.dto.ComplainQueryParam;
import com.enation.app.javashop.model.trade.complain.vo.OrderComplainVO;
import com.enation.app.javashop.model.trade.order.vo.OrderFlowNode;
import com.enation.app.javashop.model.trade.complain.dos.OrderComplain;

import java.util.List;

/**
 * 交易投诉表业务层
 * @author fk
 * @version v2.0
 * @since v2.0
 * 2019-11-27 16:48:27
 */
public interface OrderComplainManager	{

	/**
	 * 查询交易投诉表列表
	 * @param param 条件参数
	 * @return WebPage
	 */
	WebPage list(ComplainQueryParam param);
	/**
	 * 添加交易投诉表
	 * @param complain 交易投诉表
	 * @return OrderComplain 交易投诉表
	 */
	OrderComplain add(ComplainDTO complain);

	/**
	* 修改交易投诉表
	* @param orderComplain 交易投诉表
	* @param id 交易投诉表主键
	* @return OrderComplain 交易投诉表
	*/
	OrderComplain edit(OrderComplain orderComplain, Long id);
	
	/**
	 * 删除交易投诉表
	 * @param id 交易投诉表主键
	 */
	void delete(Long id);
	
	/**
	 * 获取交易投诉表
	 * @param id 交易投诉表主键
	 * @return OrderComplain  交易投诉表
	 */
	OrderComplain getModel(Long id);

	/**
	 * 撤销某个交易投诉
	 * @param id 投诉id
	 * @return
	 */
    OrderComplain cancel(Long id);

	/**
	 * 审核并交由商家申诉
	 * @param id 投诉id
	 * @return
	 */
	OrderComplain auth(Long id);

	/**
	 * 管理员仲裁结束流程
	 * @param id 投诉id
	 * @param arbitrationResult 仲裁结果
	 * @return
	 */
	OrderComplain complete(Long id, String arbitrationResult);

	/**
	 * 商家申诉
	 * @param id 投诉id
	 * @param appealContent 申诉内容
	 * @param images 图片
	 * @return
	 */
	OrderComplain appeal(Long id, String appealContent, String[] images);

	/**
	 * 提交仲裁
	 * @param id 投诉id
	 * @return
	 */
	OrderComplain arbitrate(Long id);

	/**
	 * 获取交易投诉及对话信息
	 * @param id 投诉id
	 * @return
	 */
    OrderComplainVO getModelAndCommunication(Long id);

	/**
	 * 查询交易投诉的流程图
	 * @param id 投诉id
	 * @return
	 */
	List<OrderFlowNode> getComplainFlow(Long id);
}
