package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.CommentReplyMapper;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.service.member.CommentReplyManager;
import com.enation.app.javashop.service.member.MemberCommentManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.CommentReply;
import com.enation.app.javashop.model.member.dos.MemberComment;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论回复业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 16:34:50
 */
@Service
public class CommentReplyManagerImpl implements CommentReplyManager {

    @Autowired
    private MemberCommentManager memberCommentManager;

    @Autowired
    private CommentReplyMapper commentReplyMapper;

    @Override
    public WebPage list(long page, long pageSize) {
        //获取评论回复分页列表数据
        IPage<CommentReply> iPage = commentReplyMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<>());
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        commentReplyMapper.deleteById(id);
    }

    @Override
    public CommentReply getModel(Long id) {
        return commentReplyMapper.selectById(id);
    }

    @Override
    public CommentReply getReply(Long commentId) {
        //新建查询条件包装器
        QueryWrapper<CommentReply> countWrapper = new QueryWrapper<>();
        //以评论ID为查询条件
        countWrapper.eq("comment_id", commentId);
        //查询评论回复的数量
        Integer count = commentReplyMapper.selectCount(countWrapper);
        //如果数量大于0
        if(count > 0 ){
            // 查询评论回复集合
            List<CommentReply> resList = commentReplyMapper.selectList(countWrapper);
            //目前商城只支持单次回复，一次对话
            return resList.get(0);
        }
        return null;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CommentReply replyComment(Long commentId, String reply, Permission permission) {
        //获取当前登录的商家店铺信息
        Seller seller = UserContext.getSeller();
        //根据评论ID获取会员评论信息
        MemberComment comment = this.memberCommentManager.getModel(commentId);
        //判断是否为商家，且评论属于当前登陆商家
        boolean flag = Permission.SELLER.equals(permission) && comment.getSellerId().equals(seller.getSellerId());
        if (comment == null || !flag) {
            throw new ServiceException(MemberErrorCode.E200.code(), "无权限回复");
        }

        //新建查询条件包装器
        QueryWrapper<CommentReply> countWrapper = new QueryWrapper<>();
        //以评论ID为查询条件
        countWrapper.eq("comment_id", commentId);
        //查询评论回复的数量
        Integer count = commentReplyMapper.selectCount(countWrapper);
        //目前商城只支持单次回复
        if (count > 0) {
            throw new ServiceException(MemberErrorCode.E200.code(), "不能重复回复");
        }

        //新建评论回复对象
        CommentReply commentReply = new CommentReply();
        //设置评论ID
        commentReply.setCommentId(commentId);
        //设置回复内容
        commentReply.setContent(SensitiveFilter.filter(reply, CharacterConstant.WILDCARD_STAR));
        //设置回复时间
        commentReply.setCreateTime(DateUtil.getDateline());
        //设置角色权限
        commentReply.setRole(permission.name());
        //如果是初评，则回复为初评回复，如果是追评，则回复为追评回复
        commentReply.setReplyType(comment.getCommentsType());
        //回复内容入库
        commentReplyMapper.insert(commentReply);

        //更改评论的状态为已回复
        comment.setReplyStatus(1);
        //修改会员评论内容
        this.memberCommentManager.edit(comment, commentId);

        return commentReply;
    }
}
