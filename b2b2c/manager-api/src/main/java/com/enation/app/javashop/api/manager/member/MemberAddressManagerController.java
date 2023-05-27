package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.model.member.dos.MemberAddress;
import com.enation.app.javashop.service.member.MemberAddressManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


/**
 * 会员地址api
 *
 * @author zh
 * @version v2.0
 * @since v7.0.0
 * 2018-03-18 15:37:00
 */
@RestController
@RequestMapping("/admin/members")
@Api(description = "会员地址相关API")
public class MemberAddressManagerController {

    @Autowired
    private MemberAddressManager memberAddressManager;

    @ApiOperation(value = "查询指定会员的地址列表", response = MemberAddress.class)
    @GetMapping(value = "/addresses/{member_id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "member_id", value = "会员id", required = true, dataType = "int", paramType = "path")
    })
    public WebPage list(@ApiIgnore @PathVariable("member_id") Long memberId, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        return this.memberAddressManager.list(pageNo, pageSize, memberId);
    }
}
