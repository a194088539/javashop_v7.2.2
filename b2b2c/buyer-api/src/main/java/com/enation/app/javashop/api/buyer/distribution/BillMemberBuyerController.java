package com.enation.app.javashop.api.buyer.distribution;

import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.vo.BillMemberVO;
import com.enation.app.javashop.model.distribution.vo.DistributionOrderVO;
import com.enation.app.javashop.model.distribution.vo.DistributionSellbackOrderVO;
import com.enation.app.javashop.service.distribution.BillMemberManager;
import com.enation.app.javashop.service.distribution.DistributionOrderManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.security.model.Buyer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 分销总结算
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/23 上午8:32
 */
@RestController
@Api(description = "分销总结算")
@RequestMapping("/distribution/bill")
public class BillMemberBuyerController {


    @Autowired
    private BillMemberManager billMemberManager;

    @Autowired
    private DistributionOrderManager distributionOrderManager;


    @ApiOperation("获取某会员当前月份结算单")
    @GetMapping("/member")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bill_id", value = "总结算单id", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "member_id", value = "会员id 为空时获取当前登录会员的结算单", required = false, paramType = "query", dataType = "int")
    })
    public BillMemberVO billMemberVO(@ApiIgnore Long billId, @ApiIgnore Long memberId) {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        if (memberId == null || memberId == 0) {
            memberId = buyer.getUid();
        }
        return billMemberManager.getCurrentBillMember(memberId, billId);
    }

    @ApiOperation("根据结算单获取订单信息")
    @GetMapping(value = "/order-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "分页大小", required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "bill_id", value = "总结算单id", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "member_id", value = "会员id 为0代表查看当前会员业绩", required = false, paramType = "query", dataType = "int")
    })
    public WebPage<DistributionOrderVO> orderList(@ApiIgnore Long memberId, @ApiIgnore Long billId, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        if (memberId == null || memberId == 0) {
            return distributionOrderManager.pageDistributionTotalBillOrder(pageSize, pageNo, buyer.getUid(), billId);
        }
        WebPage<DistributionOrderVO> voPage = distributionOrderManager.pageDistributionTotalBillOrder(pageSize, pageNo, memberId, billId);
        return voPage;
    }

    @ApiOperation("根据结算单获取退款订单信息")
    @GetMapping(value = "/sellback-order-list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "分页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page_no", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "bill_id", value = "结算单id", required = true, paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "member_id", value = "会员id 为0代表查看当前会员业绩", paramType = "query", dataType = "int")
    })
    public WebPage<DistributionSellbackOrderVO> sellbackOrderList(@ApiIgnore Long memberId, @ApiIgnore Long billId, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        if (memberId == null || memberId == 0) {
            return distributionOrderManager.pageSellBackOrder(pageSize, pageNo, buyer.getUid(), billId);
        }
        return distributionOrderManager.pageSellBackOrder(pageSize, pageNo, memberId, billId);
    }


    @ApiOperation("历史业绩")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page_size", value = "分页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "member_id", value = "会员id 为0或空代表查看当前会员业绩", paramType = "query", dataType = "int")
    })
    @GetMapping(value = "/history")
    public WebPage<BillMemberVO> historyBill(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore Long memberId) {
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new DistributionException(DistributionErrorCode.E1001.code(), DistributionErrorCode.E1001.des());
        }
        if (memberId == null || memberId == 0) {
            return billMemberManager.billMemberPage(pageNo, pageSize, buyer.getUid());
        }
        return billMemberManager.billMemberPage(pageNo, pageSize, memberId);
    }

}
