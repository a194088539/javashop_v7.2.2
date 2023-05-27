package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.member.dos.MemberComment;
import com.enation.app.javashop.model.member.dto.AdditionalCommentDTO;
import com.enation.app.javashop.model.member.dto.CommentQueryParam;
import com.enation.app.javashop.model.member.dto.CommentScoreDTO;
import com.enation.app.javashop.model.member.vo.BatchAuditVO;
import com.enation.app.javashop.model.member.vo.CommentVO;
import com.enation.app.javashop.model.member.vo.GoodsGrade;
import com.enation.app.javashop.model.member.vo.MemberCommentCount;
import com.enation.app.javashop.model.trade.order.dto.OrderDetailDTO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 评论业务层
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:19:14
 */
public interface MemberCommentManager {

    /**
     * 查询评论列表
     *
     * @param param 条件
     * @return WebPage
     */
    WebPage list(CommentQueryParam param);

    /**
     * 添加评论
     *
     * @param comment 评论信息
     * @param permission 权限
     * @return
     */
    MemberComment add(CommentScoreDTO comment, Permission permission);

    /**
     * 修改评论
     *
     * @param memberComment 评论
     * @param id            评论主键
     * @return MemberComment 评论
     */
    MemberComment edit(MemberComment memberComment, Long id);

    /**
     * 删除评论
     *
     * @param id 评论主键
     */
    void delete(Long id);

    /**
     * 获取评论
     *
     * @param id 评论主键
     * @return MemberComment  评论
     */
    MemberComment getModel(Long id);

    /**
     * 查询商品的好评比例
     *
     * @return
     */
    List<GoodsGrade> queryGoodsGrade();

    /**
     * 根据商品id获取评论数
     *
     * @param goodsId 商品id
     * @return 评论数
     */
    Integer getGoodsCommentCount(Long goodsId);

    /**
     * 自动好评
     */
    void autoGoodComments(List<OrderDetailDTO> detailDTOList);

    /**
     * 查询某个商品的相关评论数量
     *
     * @param goodsId 商品ID
     * @return
     */
    MemberCommentCount count(Long goodsId);

    /**
     * 根据会员id修改头像信息
     *
     * @param memberId 会员id
     * @param face     头像
     */
    void editComment(Long memberId, String face);

    /**
     * 会员追加评论
     * @param comments 追加评论集合
     * @param permission 权限
     * @return
     */
    List<AdditionalCommentDTO> additionalComments(List<AdditionalCommentDTO> comments, Permission permission);

    /**
     * 根据会员评论id获取追评信息
     * @param commentId 追加评论ID
     * @return
     */
    MemberComment getAdditionalById(Long commentId);

    /**
     * 批量审核会员商品评论
     * @param batchAuditVO 审核信息
     */
    void batchAudit(BatchAuditVO batchAuditVO);

    /**
     * 获取评论详情
     * @param commentId 评论ID
     * @return
     */
    CommentVO get(Long commentId);

    /**
     * 根据商品编号和订单编号查询评论详情
     * @param orderSn 订单编号
     * @param skuId 商品sku
     * @return
     */
    List<CommentVO> get(String orderSn,Long skuId);

}
