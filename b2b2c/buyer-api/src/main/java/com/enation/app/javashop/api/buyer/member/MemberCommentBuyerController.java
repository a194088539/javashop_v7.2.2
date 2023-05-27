package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.MemberComment;
import com.enation.app.javashop.model.member.dto.AdditionalCommentDTO;
import com.enation.app.javashop.model.member.dto.CommentQueryParam;
import com.enation.app.javashop.model.member.dto.CommentScoreDTO;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.model.member.enums.CommentTypeEnum;
import com.enation.app.javashop.model.member.vo.CommentVO;
import com.enation.app.javashop.model.member.vo.MemberCommentCount;
import com.enation.app.javashop.service.member.MemberCommentManager;
import com.enation.app.javashop.model.trade.order.enums.CommentStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * 评论控制器
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-03 10:19:14
 */
@RestController
@RequestMapping("/members/comments")
@Api(description = "评论相关API")
public class MemberCommentBuyerController {

    @Autowired
    private MemberCommentManager memberCommentManager;

    @ApiOperation(value = "查询我的评论列表", response = CommentVO.class)
    @GetMapping
    public WebPage list(@Valid CommentQueryParam param) {

        Buyer buyer = UserContext.getBuyer();
        param.setMemberId(buyer.getUid());

        return this.memberCommentManager.list(param);
    }


    @ApiOperation(value = "提交评论")
    @PostMapping
    public MemberComment addComments(@Valid @RequestBody CommentScoreDTO comment) {

        return memberCommentManager.add(comment, Permission.BUYER);
    }

    @ApiOperation(value = "查询某商品的评论", response = CommentVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goods_id", value = "商品ID", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "have_image", value = "是否有图 1：有图，0：无图", dataType = "int", paramType = "query", allowableValues = "0,1"),
            @ApiImplicitParam(name = "have_additional", value = "是否含有追评 1：是，0：否", dataType = "int", paramType = "query", allowableValues = "0,1"),
            @ApiImplicitParam(name = "grade", value = "评价等级 good：好评，neutral：中评，bad：差评", dataType = "String", paramType = "query", allowableValues = "goods,neutral,bad"),
            @ApiImplicitParam(name = "key_words", value = "关键字", dataType = "String", paramType = "query")
    })
    @GetMapping("/goods/{goods_id}")
    public WebPage list(@PathVariable("goods_id") Long goodsId, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize,
                        @ApiIgnore Integer haveImage, @ApiIgnore Integer haveAdditional, @ApiIgnore String grade, @ApiIgnore String keyWords) {

        CommentQueryParam param = new CommentQueryParam();
        param.setCommentsType(CommentTypeEnum.INITIAL.value());
        param.setAuditStatus(AuditEnum.PASS_AUDIT.value());
        param.setGoodsId(goodsId);
        param.setPageNo(pageNo);
        param.setPageSize(pageSize);
        param.setHaveImage(haveImage);
        param.setHaveAdditional(haveAdditional);
        param.setGrade(grade);
        param.setKeyword(keyWords);

        return this.memberCommentManager.list(param);
    }

    @ApiOperation(value = "查询某商品的评论数量", response = MemberCommentCount.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goods_id", value = "商品ID", required = true, paramType = "path", dataType = "int")
    })
    @GetMapping("/goods/{goods_id}/count")
    public MemberCommentCount count(@PathVariable("goods_id") Long goodsId) {

        return this.memberCommentManager.count(goodsId);
    }

    @ApiOperation(value = "提交追评", response = AdditionalCommentDTO.class)
    @PostMapping("/additional")
    public List<AdditionalCommentDTO> additionalComments(@Valid @RequestBody List<AdditionalCommentDTO> comments) {

        return memberCommentManager.additionalComments(comments, Permission.BUYER);
    }

    @ApiOperation(value = "查询追评列表，可根据状态查询", response = CommentVO.class)
    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "comment_status", value = "评论状态，WAIT_CHASE(待追评评论),FINISHED(已经完成评论)",example = "WAIT_CHASE(待追评评论),FINISHED(已经完成评论)",
                    allowableValues = "WAIT_CHASE,FINISHED",required = true, dataType = "string", paramType = "query"),

    })
    public WebPage commentList(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore String commentStatus, CommentQueryParam param) {
        Buyer buyer = UserContext.getBuyer();
        if(buyer == null ){
            throw new NoPermissionException("没有权限!");
        }

        //评论状态不能为空，且不能是未评论状态
        if(StringUtil.isEmpty(commentStatus) || CommentStatusEnum.UNFINISHED.name().equals(commentStatus)){
            throw new ServiceException(MemberErrorCode.E200.code(),"评论状态异常");
        }

        param.setMemberId(buyer.getUid());
        param.setPageNo(pageNo);
        param.setPageSize(pageSize);
        param.setCommentStatus(commentStatus);
        return this.memberCommentManager.list(param);
    }

    @ApiOperation(value = "根据订单编号和SKUid查询初评信息", response = CommentVO.class)
    @GetMapping("/detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_sn", value = "订单编号", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sku_id", value = "SKUid", required = false, dataType = "int", paramType = "query")
    })
    public List<CommentVO> commentDetail(@ApiIgnore String orderSn,@ApiIgnore Long skuId){
        return this.memberCommentManager.get(orderSn,skuId);
    }

    @ApiOperation(value = "评论id查询评论详情", response = CommentVO.class)
    @GetMapping("/{comment_id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "comment_id", value = "初评id", required = true, dataType = "int", paramType = "path")
    })
    public CommentVO commentDetail(@PathVariable("comment_id") Long commentId){
        return this.memberCommentManager.get(commentId);
    }


}
