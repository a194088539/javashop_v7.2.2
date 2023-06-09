package com.enation.app.javashop.api.manager.payment;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import com.enation.app.javashop.model.trade.order.dto.PayLogQueryParam;
import com.enation.app.javashop.service.payment.PayLogManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 付款单相关API
 * @author Snow create in 2018/7/18
 * @version v2.0
 * @since v7.0.0
 */
@Api(description = "付款单相关API")
@RestController
@RequestMapping("/admin/trade/orders")
@Validated
public class PayLogManagerController {

    @Autowired
    private PayLogManager payLogManager;

    @ApiOperation(value = "查询付款单列表")
    @GetMapping("/pay-log")
    public WebPage<PayLog> list(PayLogQueryParam param) {

        WebPage page = this.payLogManager.list(param);

        return page;
    }


    @ApiOperation(value = "收款单导出Excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name	= "order_sn", value = "订单编号",  dataType = "String",	paramType =	"query"),
            @ApiImplicitParam(name	= "pay_way", value = "支付方式",  dataType = "String",	paramType =	"query"),
            @ApiImplicitParam(name	= "start_time", value = "开始时间",  dataType = "Long",	paramType =	"query"),
            @ApiImplicitParam(name	= "end_time", value = "结束时间",  dataType = "Long",	paramType =	"query"),
            @ApiImplicitParam(name	= "member_name", value = "会员名",  dataType = "String",	paramType =	"query")
    })
    @GetMapping(value = "/pay-log/list")
    public List<PayLog> excel(@ApiIgnore String orderSn, @ApiIgnore String payWay, @ApiIgnore String memberName,
                              @ApiIgnore Long startTime, @ApiIgnore Long endTime) {

        PayLogQueryParam param = new PayLogQueryParam();
        param.setOrderSn(orderSn);
        param.setPayWay(payWay);
        param.setMemberName(memberName);
        param.setStartTime(startTime);
        param.setEndTime(endTime);

        return payLogManager.exportExcel(param);
    }


}
