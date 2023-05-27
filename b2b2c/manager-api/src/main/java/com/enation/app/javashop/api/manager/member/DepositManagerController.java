package com.enation.app.javashop.api.manager.member;

import com.enation.app.javashop.client.trade.DepositeLogClient;
import com.enation.app.javashop.client.trade.RechargeClient;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;
import com.enation.app.javashop.model.trade.deposite.RechargeDO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 预存款相关API
 * @author: liuyulei
 * @create: 2020-01-02 16:28
 * @version:1.0
 * @since:7.1.4
 **/
@RestController
@RequestMapping("/admin/members/deposit")
@Api(description = "预存款相关API")
@Validated
public class DepositManagerController {


    @Autowired
    private DepositeLogClient depositeLogClient;

    @Autowired
    private RechargeClient rechargeClient;

    @ApiOperation(value = "获取会员预存款明细", response = DepositeLogDO.class)
    @GetMapping(value = "/log")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "member_name", value = "会员名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "start_time", value = "开始时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "end_time", value = "结束时间", required = false, dataType = "long", paramType = "query"),
    })
    public WebPage listLog(@Validated DepositeParamDTO paramDTO) {
        return this.depositeLogClient.list(paramDTO);
    }


    @ApiOperation(value = "获取会员充值记录", response = RechargeDO.class)
    @GetMapping(value = "/recharge")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "member_name", value = "会员名称", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "start_time", value = "开始时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "end_time", value = "结束时间", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "sn", value = "充值编号", required = false, dataType = "string", paramType = "query"),
    })
    public WebPage listRecharge(@Validated DepositeParamDTO paramDTO) {
        return this.rechargeClient.list(paramDTO);
    }


}
