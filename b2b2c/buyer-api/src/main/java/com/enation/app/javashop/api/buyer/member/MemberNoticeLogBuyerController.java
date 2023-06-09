package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.MemberNoticeLog;
import com.enation.app.javashop.model.member.dto.MemberNoticeDTO;
import com.enation.app.javashop.service.member.MemberNoticeLogManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


/**
 * 会员站内消息历史控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-05 14:10:16
 */
@RestController
@RequestMapping("/members/member-nocice-logs")
@Api(description = "会员站内消息历史相关API")
public class MemberNoticeLogBuyerController {

    @Autowired
    private MemberNoticeLogManager memberNociceLogManager;


    @ApiOperation(value = "查询会员站内消息历史列表", response = MemberNoticeLog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "read", value = "是否已读，1已读，0未读", allowableValues = "0,1", required = false, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, Integer read) {
        return this.memberNociceLogManager.list(pageNo, pageSize, read);
    }


    @PutMapping(value = "/{ids}/read")
    @ApiOperation(value = "将消息设置为已读", response = MemberNoticeLog.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要设置为已读消息的id", required = true, dataType = "int", paramType = "path", allowMultiple = true)
    })
    public String read(@PathVariable Long[] ids) {
        this.memberNociceLogManager.read(ids);
        return null;
    }


    @DeleteMapping(value = "/{ids}")
    @ApiOperation(value = "删除会员站内消息历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的消息主键", required = true, dataType = "int", paramType = "path", allowMultiple = true)
    })
    public String delete(@PathVariable Long[] ids) {
        this.memberNociceLogManager.delete(ids);
        return null;
    }

    @ApiOperation(value = "查询会员站内消息未读消息数量", response = MemberNoticeDTO.class)
    @GetMapping("/number")
    public MemberNoticeDTO getNum() {
        return this.memberNociceLogManager.getNum();
    }
}
