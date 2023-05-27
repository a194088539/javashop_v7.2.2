package com.enation.app.javashop.api.manager.statistics;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.SearchCriteria;
import com.enation.app.javashop.model.statistics.vo.MapChartData;
import com.enation.app.javashop.model.statistics.vo.MultipleChart;
import com.enation.app.javashop.model.statistics.vo.SalesTotal;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;
import com.enation.app.javashop.service.statistics.OrderStatisticManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 订单统计
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/8 下午6:47
 */


@RestController
@Api(description = "后台统计=》其他统计")
@RequestMapping("/admin/statistics/order")
public class OrderStatisticManagerController {

    @Autowired
    private OrderStatisticManager orderAnalysisManager;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "order_status", value = "订单状态", required = false, dataType = "String", paramType = "query")
    })
    @ApiOperation("其他统计=》订单统计=》下单金额")
    @GetMapping(value = "/order/money")
    public MultipleChart getOrderMoney(@ApiIgnore SearchCriteria searchCriteria, @ApiIgnore String orderStatus) {
        return this.orderAnalysisManager.getOrderMoney(searchCriteria, orderStatus);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "order_status", value = "订单状态", required = false, dataType = "String", paramType = "query")
    })
    @ApiOperation("其他统计=》订单统计=》下单数量")
    @GetMapping(value = "/order/num")
    public MultipleChart getOrderNum(@ApiIgnore SearchCriteria searchCriteria, @ApiIgnore String orderStatus) {
        return this.orderAnalysisManager.getOrderNum(searchCriteria, orderStatus);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "order_status", value = "订单状态", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "分页大小", required = false, dataType = "int", paramType = "query")
    })
    @ApiOperation("其他统计=》订单统计=》下单数量")
    @GetMapping(value = "/order/page")
    public WebPage getOrderPage(@ApiIgnore SearchCriteria searchCriteria, @ApiIgnore String orderStatus, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        return this.orderAnalysisManager.getOrderPage(searchCriteria, orderStatus, pageNo, pageSize);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @ApiOperation("其他统计=》销售收入统计 page")
    @GetMapping(value = "/sales/money")
    public WebPage getSalesMoney(@ApiIgnore SearchCriteria searchCriteria, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        return this.orderAnalysisManager.getSalesMoney(searchCriteria,  pageNo,  pageSize);
    }


    @ApiOperation("其他统计=》销售收入 退款统计 page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/aftersales/money")
    public WebPage getAfterSalesMoney(@ApiIgnore SearchCriteria searchCriteria, @ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        return this.orderAnalysisManager.getAfterSalesMoney(searchCriteria,  pageNo,  pageSize);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @ApiOperation("其他统计=》销售收入总览")
    @GetMapping(value = "/sales/total")
    public SalesTotal getSalesMoneyTotal(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getSalesMoneyTotal(searchCriteria);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @ApiOperation("区域分析=>下单会员数")
    @GetMapping(value = "/region/member")
    public MapChartData getOrderRegionMember(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getOrderRegionMember(searchCriteria);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @ApiOperation("区域分析=>下单量")
    @GetMapping(value = "/region/num")
    public MapChartData getOrderRegionNum(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getOrderRegionNum(searchCriteria);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @ApiOperation("区域分析=>下单金额")
    @GetMapping(value = "/region/money")
    public MapChartData getOrderRegionMoney(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getOrderRegionMoney(searchCriteria);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query")
    })
    @ApiOperation("区域分析表格=>page")
    @GetMapping(value = "/region/form")
    public WebPage getOrderRegionForm(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getOrderRegionForm(searchCriteria);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "prices", value = "价格区间", required = false, paramType = "query", dataType = "int", allowMultiple = true)
    })
    @ApiOperation("客单价分布=>客单价分布")
    @GetMapping(value = "/unit/price")
    public SimpleChart getUnitPrice(@ApiIgnore SearchCriteria searchCriteria, @RequestParam(required = false) Integer[] prices) {
        return this.orderAnalysisManager.getUnitPrice(searchCriteria, prices);
    }

    @ApiOperation("客单价分布=>购买频次分析")
    @GetMapping(value = "/unit/num")
    public WebPage getUnitNum() {
        return this.orderAnalysisManager.getUnitNum();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id", value = "店铺id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "prices", value = "价格区间", required = false, paramType = "query", dataType = "int", allowMultiple = true)
    })
    @ApiOperation("客单价分布=>购买时段分析")
    @GetMapping(value = "/unit/time")
    public SimpleChart getUnitTime(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getUnitTime(searchCriteria);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "cycle_type", value = "日期类型", dataType = "String", paramType = "query", required = true, allowableValues = "YEAR,MONTH"),
            @ApiImplicitParam(name = "year", value = "年份", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "month", value = "月份", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "seller_id",value = "店铺id",dataType = "int",paramType = "query")
    })
    @ApiOperation("退款统计")
    @GetMapping(value = "/return/money")
    public SimpleChart getReturnMoney(@ApiIgnore SearchCriteria searchCriteria) {
        return this.orderAnalysisManager.getReturnMoney(searchCriteria);
    }

}
