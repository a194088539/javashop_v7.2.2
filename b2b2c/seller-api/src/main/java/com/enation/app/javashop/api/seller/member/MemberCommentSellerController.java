package com.enation.app.javashop.api.seller.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.member.dos.CommentReply;
import com.enation.app.javashop.model.member.dto.CommentQueryParam;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.model.member.vo.CommentVO;
import com.enation.app.javashop.service.member.CommentReplyManager;
import com.enation.app.javashop.service.member.MemberCommentManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 评论控制器
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:19:14
 */
@RestController
@RequestMapping("/seller/members/comments")
@Api(description = "评论相关API")
@Validated
public class MemberCommentSellerController {

    @Autowired
    private MemberCommentManager memberCommentManager;
    @Autowired
    private CommentReplyManager commentReplyManager;


    @ApiOperation(value = "查询评论列表", response = CommentVO.class)
    @GetMapping
    public WebPage list(@Valid CommentQueryParam param) {
        param.setAuditStatus(AuditEnum.PASS_AUDIT.value());
        param.setSellerId(UserContext.getSeller().getSellerId());

        return this.memberCommentManager.list(param);
    }

    @ApiOperation(value = "回复评论", notes = "商家回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "comment_id", value = "评论id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "reply", value = "商家回复内容", required = true, dataType = "String", paramType = "query")})
    @PostMapping(value = "/{comment_id}/reply")
    public CommentReply replyComment(@PathVariable(name = "comment_id") Long commentId, @NotEmpty(message = "回复内容不能为空") String reply) {

        CommentReply commentReply = this.commentReplyManager.replyComment(commentId,reply,Permission.SELLER);

        return commentReply;
    }

    @ApiOperation(value = "查询商品评论详请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "comment_id", value = "主键ID", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping("/{comment_id}")
    public CommentVO get(@PathVariable("comment_id") Long commentId) {
        return this.memberCommentManager.get(commentId);
    }

}
