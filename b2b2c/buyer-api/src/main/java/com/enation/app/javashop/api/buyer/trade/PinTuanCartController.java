package com.enation.app.javashop.api.buyer.trade;

import com.enation.app.javashop.service.trade.pintuan.PintuanCartManager;
import com.enation.app.javashop.service.trade.pintuan.impl.PintuanTradeManagerImpl;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo;
import com.enation.app.javashop.model.trade.cart.vo.CartView;
import com.enation.app.javashop.model.trade.order.vo.TradeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

/**
 * Created by kingapex on 2019-01-23.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2019-01-23
 */

@Api(description = "拼团购物API")
@RestController
@RequestMapping("/pintuan")
public class PinTuanCartController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PintuanCartManager pintuanCartManager;

    @Autowired
    private PintuanTradeManagerImpl pintuanTradeManagerImpl;

    @ApiOperation(value = "获取购物车页面购物车详情")
    @GetMapping("/cart")
    public CartView cart() {

        CartView cartView = pintuanCartManager.getCart();
        logger.debug("cartView:"+cartView);

        return cartView;
    }


    @ApiOperation(value = "向拼团购物车中加一个sku")
    @PostMapping("/cart/sku")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sku_id", value = "sku ID", dataType = "int", paramType = "query", required = true),
            @ApiImplicitParam(name = "num", value = "购买数量", dataType = "int", paramType = "query")
    })
    public CartSkuOriginVo addSku(@ApiIgnore @NotNull(message = "sku id不能为空") Long skuId, Integer num) {
        CartSkuOriginVo cartSkuOriginVo = pintuanCartManager.addSku(skuId, num);
        return cartSkuOriginVo;
    }


    @ApiOperation(value = "创建交易")
    @PostMapping(value = "/trade")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "client", value = "客户端类型", required = false, dataType = "String", paramType = "query", allowableValues = "PC,WAP,NATIVE,REACT"),
            @ApiImplicitParam(name = "pintuan_order_id", value = "拼团订单id，如果为空创建拼团，如果不为空参团", required = false, dataType = "int", paramType = "query")
    })
    public TradeVO create(@ApiIgnore String client, @ApiIgnore Long pintuanOrderId) {
        TradeVO tradeVO = this.pintuanTradeManagerImpl.createTrade(client, pintuanOrderId);
        return tradeVO;
    }

}
