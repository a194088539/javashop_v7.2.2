package com.enation.app.javashop.api.manager.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.errorcode.DistributionErrorCode;
import com.enation.app.javashop.service.distribution.exception.DistributionException;
import com.enation.app.javashop.service.distribution.DistributionStatisticManager;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
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

/**
 * 统计
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/24 下午3:15
 */
@RestController
@RequestMapping("/admin/distribution/statistic")
@Api(description = "统计")
public class DistributionStatisticManagerController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DistributionStatisticManager distributionStatisticManager;

    @ApiOperation("订单金额统计")
    @GetMapping("/order")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circle", value = "搜索类型：YEAR/MONTH", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "member_id", value = "会员id", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "year", value = "年份", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "month", value = "月份", paramType = "query", dataType = "int")
    })
    public SimpleChart order(String circle, Long memberId, Integer year, Integer month) {
        try {
            if (memberId == null) {
                throw new DistributionException(DistributionErrorCode.E1011.code(), DistributionErrorCode.E1011.des());
            }
            return distributionStatisticManager.getOrderMoney(circle, memberId, year, month);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("统计金额异常：", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

    @ApiOperation("订单数量统计")
    @GetMapping("/count")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circle", value = "搜索类型：YEAR/MONTH", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "member_id", value = "会员id", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "year", value = "年份", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "month", value = "月份", paramType = "query", dataType = "int")
    })
    public SimpleChart count(String circle, Long memberId, Integer year, Integer month) {
        try {
            if (memberId == null) {
                throw new DistributionException(DistributionErrorCode.E1011.code(), DistributionErrorCode.E1011.des());
            }
            return distributionStatisticManager.getOrderCount(circle, memberId, year, month);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("统计金额异常：", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }

    @ApiOperation("订单返现统计")
    @GetMapping("/push")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circle", value = "搜索类型：YEAR/MONTH", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "member_id", value = "会员id", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "year", value = "年份", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "month", value = "月份", paramType = "query", dataType = "int")
    })
    public SimpleChart push(String circle, Long memberId, Integer year, Integer month) {
        try {
            if (memberId == null) {
                throw new DistributionException(DistributionErrorCode.E1011.code(), DistributionErrorCode.E1011.des());
            }
            return distributionStatisticManager.getPushMoney(circle, memberId, year, month);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("统计金额异常：", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


    @ApiOperation("店铺返现统计")
    @GetMapping("/push/seller")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "circle", value = "搜索类型：YEAR/MONTH", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "year", value = "年份", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "month", value = "月份", paramType = "query", dataType = "int")
    })
    public WebPage pushShop(String circle, Integer year, Integer month, Long pageSize, Long pageNo) {
        try {
            return distributionStatisticManager.getShopPush(circle, year, month, pageSize, pageNo);
        } catch (DistributionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("统计金额异常：", e);
            throw new DistributionException(DistributionErrorCode.E1000.code(), DistributionErrorCode.E1000.des());
        }
    }


}
