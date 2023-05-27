package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.service.trade.order.OrderPayManager;
import com.enation.app.javashop.model.trade.deposite.RechargeDO;
import com.enation.app.javashop.service.trade.deposite.RechargeManager;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Map;

/**
 * @description: 预存款充值
 * @author: liuyulei
 * @create: 2019-12-30 19:53
 * @version:1.0
 * @since:7.1.4
 **/
@Api(description = "预存款充值相关API")
@RestController
@RequestMapping("/recharge")
@Validated
public class RechargePayBuyerController {

    @Autowired
    private RechargeManager rechargeManager;

    @Autowired
    private OrderPayManager orderPayManager;

    @PostMapping
    @ApiOperation(value	= "创建充值订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "price", value = "充值金额", required = true, dataType = "double", paramType = "query")
    })
    public RechargeDO create(@Max(value = 10000,message = "充值金额输入有误，单次最多允许充值10000元") @Min(value = 1, message = "充值金额有误，单次最少充值金额为1元") Double price)	{
        return this.rechargeManager.recharge(price);
    }


    @PostMapping(value = "/{sn}")
    @ApiOperation(value	= "支付充值订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "充值订单编号", required = true, dataType = "String", paramType = "path")
    })
    public Map pay(@PathVariable(name = "sn") String sn,  @Validated PayParam payParam)	{
        payParam.setSn(sn);
        payParam.setTradeType(TradeTypeEnum.RECHARGE.name());
        return orderPayManager.pay(payParam);
    }

}
