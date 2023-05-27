package com.enation.app.javashop.service.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.MemberAsk;
import com.enation.app.javashop.model.member.dto.AskQueryParam;
import com.enation.app.javashop.model.member.vo.BatchAuditVO;
import com.enation.app.javashop.model.member.vo.MemberAskVO;

import java.util.List;

/**
 * 咨询业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-04 17:41:18
 */
public interface MemberAskManager	{

	/**
	 * 查询咨询列表
	 * @param param 查询条件
	 * @return WebPage
	 */
	WebPage list(AskQueryParam param);
	/**
	 * 添加咨询
	 * @param askContent 咨询
	 * @param goodsId 商品id
	 * @param anonymous 是否匿名 YES:是，NO:否
	 * @return MemberAsk 咨询
	 */
	MemberAsk add(String askContent, Long goodsId, String anonymous);

	/**
	 * 获取会员商品咨询详情
	 * @param askId 会员商品咨询主键id
	 * @return MemberAsk  咨询
	 */
	MemberAsk getModel(Long askId);

	/**
	 * 获取会员商品咨询详情
	 * 包含回复信息和商品信息
	 * @param askId 会员商品咨询主键id
	 * @return
	 */
	MemberAskVO getModelVO(Long askId);

	/**
	 * 获取商品会员咨询分页列表数据
	 * @param pageNo 页数
	 * @param pageSize 每页记录数
	 * @param goodsId 商品id
	 * @return
	 */
	WebPage listGoodsAsks(Long pageNo, Long pageSize, Long goodsId);

	/**
	 * 获取与会员商品咨询相关的其它咨询
	 * @param askId 问题咨询ID
	 * @param goodsId 商品ID
	 * @return
	 */
	List<MemberAsk> listRelationAsks(Long askId, Long goodsId);

	/**
	 * 批量审核会员商品咨询
	 * @param batchAuditVO 审核信息
	 */
	void batchAudit(BatchAuditVO batchAuditVO);

	/**
	 * 删除会员商品咨询
	 * @param askId 会员商品咨询主键
	 */
	void delete(Long askId);

	/**
	 * 商家回复会员商品咨询
	 * @param replyContent 回复内容
	 * @param askId 会员商品咨询id
	 * @return
	 */
	MemberAsk reply(String replyContent, Long askId);

	/**
	 * 修改会员商品咨询回复数量
	 * @param askId 会员商品咨询ID
	 * @param num 数量
	 */
	void updateReplyNum(Long askId, Integer num);

	/**
	 * 卖家获取未回复的咨询数量
	 * @param sellerId 商家ID
	 * @return
	 */
	Integer getNoReplyCount(Long sellerId);

}
