package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.member.dos.AskReplyDO;
import com.enation.app.javashop.model.member.dos.MemberAsk;
import com.enation.app.javashop.model.member.dto.ReplyQueryParam;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.model.member.enums.CommonStatusEnum;
import com.enation.app.javashop.model.member.vo.AskReplyVO;
import com.enation.app.javashop.service.member.AskReplyManager;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.framework.context.user.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 会员商品咨询回复API
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-17
 */
@RestController
@RequestMapping("/members/asks/reply")
@Api(description = "会员商品咨询回复API")
@Validated
public class AskReplyBuyerController {

    @Autowired
    private AskReplyManager askReplyManager;

    @ApiOperation(value = "查询某条会员商品咨询回复列表", response = AskReplyDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "ask_id", value = "会员商品咨询id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "reply_id", value = "会员商品咨询回复id", dataType = "int", paramType = "path")
    })
    @GetMapping("/list/{ask_id}")
    public WebPage listReply(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @PathVariable("ask_id")Long askId, @ApiIgnore Integer replyId) {

        ReplyQueryParam param = new ReplyQueryParam();
        param.setPageNo(pageNo);
        param.setPageSize(pageSize);
        param.setAskId(askId);
        param.setReplyId(replyId);
        param.setAuthStatus(AuditEnum.PASS_AUDIT.value());
        param.setReplyStatus(CommonStatusEnum.YES.value());

        return this.askReplyManager.list(param);
    }

    @ApiOperation(value = "查询会员商品咨询回复列表", response = AskReplyVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "reply_status", value = "是否已回复 YES：是，NO：否", dataType = "String", paramType = "query")
    })
    @GetMapping("/list/member")
    public WebPage listMemberReply(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore String replyStatus) {

        ReplyQueryParam param = new ReplyQueryParam();
        param.setPageNo(pageNo);
        param.setPageSize(pageSize);
        param.setReplyStatus(replyStatus);
        param.setMemberId(UserContext.getBuyer().getUid());

        return this.askReplyManager.listMemberReply(param);
    }

    @ApiOperation(value = "回复会员商品咨询", response = AskReplyDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ask_id", value = "会员商品咨询id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "reply_content", value = "回复内容", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "anonymous", value = "是否匿名 YES:是，NO:否", required = true, dataType = "string", paramType = "query", allowableValues = "YES,NO")
    })
    @PostMapping("/{ask_id}")
    public AskReplyDO add(@PathVariable("ask_id")Long askId, @NotEmpty(message = "请输入内容")@ApiIgnore String replyContent, @NotNull(message = "请选择是否匿名") @ApiIgnore String anonymous) {

        //咨询回复敏感词过滤
        replyContent = SensitiveFilter.filter(replyContent, CharacterConstant.WILDCARD_STAR);

        return this.askReplyManager.updateReply(askId, replyContent, anonymous);
    }

    @ApiOperation(value = "删除回复", response = MemberAsk.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "会员商品咨询回复id", dataType = "int", paramType = "path"),
    })
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {

        this.askReplyManager.delete(id);

        return "";
    }
}
