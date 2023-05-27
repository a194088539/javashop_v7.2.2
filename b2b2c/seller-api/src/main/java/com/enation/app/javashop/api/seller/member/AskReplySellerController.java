package com.enation.app.javashop.api.seller.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.AskReplyDO;
import com.enation.app.javashop.model.member.dto.ReplyQueryParam;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.service.member.AskReplyManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 会员商品咨询回复API
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-17
 */
@RestController
@RequestMapping("/seller/members/reply")
@Api(description = "会员商品咨询回复API")
@Validated
public class AskReplySellerController {

    @Autowired
    private AskReplyManager askReplyManager;

    @ApiOperation(value = "查询会员商品咨询回复列表", response = AskReplyDO.class)
    @GetMapping
    public WebPage list(@Valid ReplyQueryParam param) {

        param.setAuthStatus(AuditEnum.PASS_AUDIT.value());

        return this.askReplyManager.list(param);
    }
}
