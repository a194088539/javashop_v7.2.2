package com.enation.app.javashop.service.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.member.dos.CommentReply;

/**
 * 评论回复业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 16:34:50
 */
public interface CommentReplyManager	{

	/**
	 * 查询评论回复列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);

	/**
	 * 删除评论回复
	 * @param id 评论回复主键
	 */
	void delete(Long id);

	/**
	 * 获取评论回复
	 * @param id 评论回复主键
	 * @return CommentReply  评论回复
	 */
	CommentReply getModel(Long id);

	/**
	 * 查询评论的相关回复
	 * @param commentId 评论ID
	 * @return
	 */
	CommentReply getReply(Long commentId);

	/**
	 * 回复评论
	 * @param commentId 评论ID
	 * @param reply 回复内容
	 * @param permission 权限
	 * @return
	 */
	CommentReply replyComment(Long commentId, String reply, Permission permission);
}
