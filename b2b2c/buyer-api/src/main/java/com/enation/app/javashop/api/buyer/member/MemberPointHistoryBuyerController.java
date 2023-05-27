package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.vo.MemberPointVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;

import com.enation.app.javashop.model.member.dos.MemberPointHistory;
import com.enation.app.javashop.service.member.MemberPointHistoryManager;

/**
 * 会员积分表控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-03 15:44:12
 */
@RestController
@RequestMapping("/members")
@Api(description = "会员积分相关API")
public class MemberPointHistoryBuyerController {

    @Autowired
    private MemberPointHistoryManager memberPointHistoryManager;
    @Autowired
    private MemberManager memberManager;


    @ApiOperation(value = "查询会员积分列表", response = MemberPointHistory.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("/points")
    public WebPage list(@ApiIgnore @NotEmpty(message = "页码不能为空") Long pageNo, @ApiIgnore @NotEmpty(message = "每页数量不能为空") Long pageSize) {
        Buyer buyer = UserContext.getBuyer();
        return this.memberPointHistoryManager.list(pageNo, pageSize, buyer.getUid());
    }


    @ApiOperation(value = "查询当前会员的积分")
    @GetMapping("/points/current")
    public MemberPointVO getPoint() {
        return memberManager.getMemberPoint();

    }

}
