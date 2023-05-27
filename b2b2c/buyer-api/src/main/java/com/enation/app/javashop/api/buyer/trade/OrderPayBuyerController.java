package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.service.trade.order.OrderPayManager;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
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

import javax.validation.Valid;
import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description: 订单支付
 * @date 2018/4/1616:44
 * @since v7.0.0
 */
@Api(description = "订单支付API")
@RestController
@RequestMapping("/order/pay")
@Validated
public class OrderPayBuyerController {

    @Autowired
    private OrderPayManager orderPayManager;

    @Autowired
    private OrderQueryManager orderQueryManager;


    @ApiOperation(value = "订单检查 是否需要支付 为false代表不需要支付，出现支付金额为0，或者已经支付，为true代表需要支付")
    @GetMapping(value = "/needpay/{sn}")
    public boolean check(@PathVariable(name = "sn") String sn) {
        OrderDetailVO order = this.orderQueryManager.getModel(sn, null);
        return order.getNeedPayMoney() != 0 && !order.getPayStatus().equals(PayStatusEnum.PAY_YES.value());
    }

    @ApiOperation(value = "对一个交易发起支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "要支付的交易sn", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "trade_type", value = "交易类型", required = true, dataType = "String", paramType = "path", allowableValues = "TRADE,ORDER")
    })
    @GetMapping(value = "/{trade_type}/{sn}")
    public Map payTrade(@PathVariable(name = "sn") String sn, @PathVariable(name = "trade_type") String tradeType, @Valid PayParam param) {

        param.setSn(sn);
        param.setTradeType(tradeType.toUpperCase());
        return orderPayManager.pay(param);
    }

    @ApiOperation(value = "APP对一个交易发起支付")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sn", value = "要支付的交易sn", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "trade_type", value = "交易类型", required = true, dataType = "String", paramType = "path", allowableValues = "TRADE,ORDER")
    })
    @GetMapping(value = "/app/{trade_type}/{sn}")
    public Map appPayTrade(@PathVariable(name = "sn") String sn, @PathVariable(name = "trade_type") String tradeType, @Valid PayParam param) {

        param.setSn(sn);
        param.setTradeType(tradeType.toUpperCase());
        return orderPayManager.pay(param);
    }

}
