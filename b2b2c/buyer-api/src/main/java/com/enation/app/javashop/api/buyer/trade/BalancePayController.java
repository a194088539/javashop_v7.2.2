package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;
import com.enation.app.javashop.service.trade.order.BalanceManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.security.model.Buyer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;

/**
 * @description: 预存款支付相关
 * @author: liuyulei
 * @create: 2020-01-01 11:37
 * @version:1.0
 * @since:7.1.4
 **/
@Api(description = "预存款支付API")
@RestController
@RequestMapping("/balance/pay")
@Validated
public class BalancePayController {

    @Autowired
    private BalanceManager balanceManager;


    @ApiOperation(value = "使用预存款支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "要支付的交易sn", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "trade_type", value = "交易类型", required = true, dataType = "String", paramType = "query", allowableValues = "TRADE,ORDER"),
            @ApiImplicitParam(name = "password", value = "支付密码", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping(value = "/{trade_type}/{sn}")
    public BalancePayVO payTrade(@PathVariable(name = "sn") String sn, @PathVariable(name = "trade_type") String tradeType,
                                 @NotEmpty(message = "密码不能为空") String password) {
        Buyer buyer = UserContext.getBuyer();
        return balanceManager.balancePay(sn,buyer.getUid(),tradeType.toUpperCase(),password);
    }
}
