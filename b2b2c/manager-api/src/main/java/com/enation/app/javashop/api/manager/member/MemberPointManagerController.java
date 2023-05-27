package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberPointHistory;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberPointHistoryManager;
import com.enation.app.javashop.service.member.MemberPointManager;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.security.model.Admin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 会员积分后台管理API
 *
 * @author zh
 * @version v7.0
 * @date 18/7/14 下午2:05
 * @since v7.0
 */
@RestController
@RequestMapping("/admin/members/point")
@Validated
@Api(description = "会员积分后台管理API")
public class MemberPointManagerController {

    @Autowired
    private MemberPointManager memberPointManager;
    @Autowired
    private MemberManager memberManager;

    @Autowired
    MemberPointHistoryManager memberPointHistoryManager;

    @PutMapping(value = "/{member_id}")
    @ApiOperation(value = "修改会消费积分", response = Member.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "member_id", value = "会员id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "point", value = "调整后的会员消费积分", required = true, dataType = "int", paramType = "query")
    })
    public void editPoint(@PathVariable("member_id") Long memberId, @Min(value = 0, message = "消费积分不能小于0") Integer point) {
        //获取当前会员的积分 如果当前会员积分大于调整后的积分 则为消费，反之则为新增
        Member member = memberManager.getModel(memberId);
        Long currentPoint = member.getConsumPoint();
        //增加或者消费的积分数
        Long operationPoint = point - currentPoint;
        //操作类型  1为加积分 0为减积分或无操作
        Integer type = 0;
        if (operationPoint > 0) {
            type = 1;
        }
        Admin admin = AdminUserContext.getAdmin();
        MemberPointHistory memberPointHistory = new MemberPointHistory();
        memberPointHistory.setMemberId(memberId);
        memberPointHistory.setGradePointType(0);
        memberPointHistory.setGradePoint(0);
        memberPointHistory.setConsumPoint(Math.abs(operationPoint));
        memberPointHistory.setConsumPointType(type);
        memberPointHistory.setReason("管理员手工修改");
        memberPointHistory.setOperator(admin.getUsername());
        memberPointManager.pointOperation(memberPointHistory);

    }

    @ApiOperation(value = "查询会员积分列表", response = MemberPointHistory.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "member_id", value = "会员id", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping("/{member_id}")
    public WebPage list(@PathVariable("member_id") Long memberId, @ApiIgnore @NotNull(message = "页码不能为空") Long pageNo, @ApiIgnore @NotNull(message = "每页数量不能为空") Long pageSize) {
        return this.memberPointHistoryManager.list(pageNo, pageSize, memberId);
    }


}
