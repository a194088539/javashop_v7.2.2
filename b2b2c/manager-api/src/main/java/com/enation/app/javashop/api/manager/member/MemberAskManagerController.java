package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.MemberAsk;
import com.enation.app.javashop.model.member.dto.AskQueryParam;
import com.enation.app.javashop.model.member.vo.BatchAuditVO;
import com.enation.app.javashop.service.member.MemberAskManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 会员商品咨询API
 *
 * @author duanmingyu
 * @version v2.0
 * @since v7.1.5
 * 2019-09-17
 */
@RestController
@RequestMapping("/admin/members/asks")
@Api(description = "会员商品咨询API")
@Validated
public class MemberAskManagerController {

    @Autowired
    private MemberAskManager memberAskManager;


    @ApiOperation(value = "查询咨询列表", response = MemberAsk.class)
    @GetMapping
    public WebPage list(@Valid AskQueryParam param) {

        return this.memberAskManager.list(param);
    }

    @ApiOperation(value = "批量审核会员商品咨询")
    @PostMapping("/batch/audit")
    public String batchAuditAsk(@Valid @RequestBody BatchAuditVO batchAuditVO) {

        this.memberAskManager.batchAudit(batchAuditVO);

        return "";
    }

    @ApiOperation(value = "删除咨询", response = MemberAsk.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ask_id", value = "会员商品咨询id", dataType = "int", paramType = "path"),
    })
    @DeleteMapping("/{ask_id}")
    public String delete(@PathVariable("ask_id")Long askId) {

        this.memberAskManager.delete(askId);

        return "";
    }

    @ApiOperation(value = "查询会员商品咨询详请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ask_id", value = "主键ID", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping("/{ask_id}")
    public MemberAsk get(@PathVariable("ask_id")Long askId) {
        return this.memberAskManager.getModel(askId);
    }

}
