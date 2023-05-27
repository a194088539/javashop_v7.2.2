package com.enation.app.javashop.api.buyer.payment;

import com.enation.app.javashop.model.base.DomainHelper;
import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.payment.enums.PayMode;
import com.enation.app.javashop.model.payment.vo.PaymentMethodVO;
import com.enation.app.javashop.service.payment.PaymentManager;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.framework.logs.Debugger;
import com.enation.app.javashop.framework.util.AbstractRequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @description: 支付公用
 * @author: liuyulei
 * @create: 2019-12-30 19:46
 * @version:1.0
 * @since:7.1.4
 **/
@Api(description = "交易支付API")
@RestController
@RequestMapping("/payment")
@Validated
public class PaymentController {


    @Autowired
    private Debugger debugger;

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private DomainHelper domainHelper;


    @ApiOperation(value = "查询支持的支付方式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "client_type", value = "调用客户端PC,WAP,NATIVE,REACT", required = true, dataType = "String", paramType = "path", allowableValues = "PC,WAP,NATIVE,REACT")
    })
    @GetMapping(value = "/{client_type}")
    public List<PaymentMethodVO> queryPayments(@PathVariable(name = "client_type") String clientType) {
        List<PaymentMethodVO> list = paymentManager.queryPayments(clientType);
        return list;
    }

    @ApiIgnore
    @ApiOperation(value = "接收支付异步回调")
    @RequestMapping(value = "/callback/{trade_type}/{plugin_id}/{client_type}")
    public String payCallback(@PathVariable(name = "trade_type") String tradeType, @PathVariable(name = "plugin_id") String paymentPluginId,
                              @PathVariable(name = "client_type") String clientType) {

        debugger.log("接收到回调消息");
        debugger.log("tradeType:[" + tradeType + "],paymentPluginId:[" + paymentPluginId + "],clientType:[" + clientType + "]");

        String result = this.paymentManager.payCallback( paymentPluginId, ClientType.valueOf(clientType));

        return result;
    }

    @ApiIgnore
    @ApiOperation(value = "接收支付同步回调")
    @GetMapping(value = "/return/{trade_type}/{pay_mode}/{plugin_id}", produces = MediaType.TEXT_HTML_VALUE)
    public String payReturn(@PathVariable(name = "trade_type") String tradeType, @PathVariable(name = "plugin_id") String paymentPluginId,
                            @PathVariable(name = "pay_mode") String payMode, HttpServletResponse response) {

        this.paymentManager.payReturn(TradeTypeEnum.valueOf(tradeType), paymentPluginId);

        String serverName = domainHelper.getBuyerDomain();
        if (AbstractRequestUtil.isMobile()) {
            serverName = domainHelper.getMobileDomain();
        }

        String url = serverName + "/payment-complete?type=" + tradeType;
        String jumpHtml = "<script>";
        //扫码支付
        if (PayMode.qr.name().equals(payMode)) {
            //二维码模式嵌在的iframe中的，要设置此相应允许被buyer域名的frame嵌套
            jumpHtml += "window.parent.location.href='" + url + "'";
        } else {
            jumpHtml += "location.href='" + url + "'";
        }

        jumpHtml += "</script>";

        return jumpHtml;
    }

    @ApiOperation(value = "主动查询支付结果")
    @GetMapping(value = "/order/pay/query/{trade_type}/{sn}")
    public String query(@PathVariable(name = "trade_type") String tradeType, @Valid PayParam param,
                        @PathVariable(name = "sn") String sn) {

        String result = this.paymentManager.queryResult(sn,tradeType);

        return result;
    }


}
