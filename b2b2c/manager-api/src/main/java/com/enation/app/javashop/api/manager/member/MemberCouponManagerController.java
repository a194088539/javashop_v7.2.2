package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.service.member.MemberCouponManager;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 会员优惠券
 *
 * @author Snow create in 2018/6/13
 * @version v2.0
 * @since v7.0.0
 */
@RestController
@RequestMapping("/admin/members/coupon")
@Api(description = "会员优惠券相关API")
@Validated
public class MemberCouponManagerController {

    @Autowired
    private MemberCouponManager memberCouponManager;


    @ApiOperation(value = "查询某优惠券领取列表", response = CouponDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "coupon_id", value = "开始时间", dataType = "int", paramType = "query"),
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize,
                        @ApiIgnore Long couponId) {

        return this.memberCouponManager.queryByCouponId(couponId,pageNo,pageSize);
    }


    @ApiOperation(value = "废弃某优惠券", response = CouponDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "coupon_id", value = "开始时间", dataType = "int", paramType = "query"),
    })
    @PutMapping("/{member_coupon_id}/cancel")
    public String list(@ApiIgnore @PathVariable("member_coupon_id") Long memberCouponId) {

        this.memberCouponManager.cancel(memberCouponId);

        return "";
    }


}
