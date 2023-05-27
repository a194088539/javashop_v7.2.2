package com.enation.app.javashop.api.manager.distribution;

import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.model.distribution.vo.DistributionOrderVO;
import com.enation.app.javashop.model.distribution.vo.DistributionSellbackOrderVO;
import com.enation.app.javashop.service.distribution.DistributionOrderManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 分销订单订单
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/28 上午11:12
 */
@Api(description = "分销订单")
@RestController
@RequestMapping("/admin/distribution/order")
public class DistributionOrderManagerController {
    @Autowired
    private DistributionOrderManager distributionOrderManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation("结算单 分销订单查询")
    @GetMapping()
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bill_id", value = "会员结算单id", required = false, paramType = "query", dataType = "long", allowMultiple = false),
            @ApiImplicitParam(name = "member_id", value = "会员id", required = false, paramType = "query", dataType = "long", allowMultiple = false),
            @ApiImplicitParam(name = "page_size", value = "页码大小", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int", allowMultiple = false),
    })
    public WebPage<DistributionOrderVO> billOrder(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore Long billId, @ApiIgnore Long memberId) throws Exception {
        try {
            return distributionOrderManager.pageDistributionOrder(pageSize, pageNo, memberId, billId);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            this.logger.error("查询结算单-》订单异常", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


    @ApiOperation("结算单 分销退款订单查询")
    @GetMapping("/sellback")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bill_id", value = "结算单id", required = false, paramType = "query", dataType = "long", allowMultiple = false),
            @ApiImplicitParam(name = "member_id", value = "会员id", required = false, paramType = "query", dataType = "long", allowMultiple = false),
            @ApiImplicitParam(name = "page_size", value = "页码大小", required = false, paramType = "query", dataType = "int", allowMultiple = false),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, paramType = "query", dataType = "int", allowMultiple = false),
    })
    public WebPage<DistributionSellbackOrderVO> billSellbackOrder(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore Long billId, @ApiIgnore Long memberId) {
        try {
            return distributionOrderManager.pageSellBackOrder(pageSize, pageNo, memberId, billId);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            this.logger.error("查询结算单-》退款单异常", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


}
