package com.enation.app.javashop.api.manager.promotion;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.coupon.dto.CouponParams;
import com.enation.app.javashop.model.promotion.coupon.enums.CouponType;
import com.enation.app.javashop.service.promotion.coupon.CouponManager;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 优惠券控制器
 *
 * @author Snow
 * @version v2.0
 * @since v7.0.0
 * 2018-04-17 23:19:39
 */
@RestController
@RequestMapping("/admin/promotion/coupons")
@Api(description = "优惠券相关API")
@Validated
public class CouponManagerController {

    @Autowired
    private CouponManager couponManager;

    @ApiOperation(value = "查询优惠券列表", response = CouponDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "start_time", value = "开始时间", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "end_time", value = "截止时间", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize,
                        @ApiIgnore CouponParams params) {

        params.setPageNo(pageNo);
        params.setPageSize(pageSize);
        params.setSellerId(0L);

        return this.couponManager.list(params);
    }


    @ApiOperation(value = "添加优惠券", response = CouponDO.class)
    @PostMapping
    public CouponDO add(@Valid CouponDO couponDO) {

        //平台添加优惠券
        couponDO.setSellerId(0L);
        couponDO.setSellerName("平台优惠券");
        //平台优惠券只有免费领取
        couponDO.setType(CouponType.FREE_GET.name());

        this.couponManager.add(couponDO);

        return couponDO;
    }

    @PutMapping(value = "/{coupon_id}")
    @ApiOperation(value = "修改优惠券", response = CouponDO.class)
    @ApiImplicitParam(name = "coupon_id", value = "优惠券id", required = true, dataType = "int", paramType = "path")
    public CouponDO add(@ApiIgnore @PathVariable("coupon_id") Long couponId, @Valid CouponDO couponDO) {


        couponDO.setCouponId(couponId);

        couponDO.setSellerId(0L);
        couponDO.setSellerName("平台优惠券");

        this.couponManager.edit(couponDO, couponId);

        return couponDO;
    }


    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的优惠券主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {

        this.couponManager.verifyAuth(id);
        this.couponManager.delete(id);

        return "";
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个优惠券")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的优惠券主键", required = true, dataType = "int", paramType = "path")
    })
    public CouponDO get(@PathVariable Long id) {

        CouponDO coupon = this.couponManager.getModel(id);
        if (coupon == null || coupon.getSellerId() != 0) {
            throw new NoPermissionException("无权操作或者数据不存在");
        }

        return coupon;
    }

}
