package com.enation.app.javashop.api.seller.trade;

import com.enation.app.javashop.model.trade.order.vo.InvoiceVO;
import com.enation.app.javashop.service.trade.order.OrderQueryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发货单控制器
 *
 * @author zh
 * @version v7.0
 * @date 2019/11/5 10:31 AM
 * @since v7.0
 */
@Api(description = "发货单API")
@RestController
@RequestMapping("/seller/trade/invoice")
@Validated
public class InvoiceSellerController {


    @Autowired
    private OrderQueryManager orderQueryManager;


    @ApiOperation(value = "查询发货单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_id", value = "订单Id", required = true, dataType = "int", paramType = "query"),
    })
    @GetMapping
    public InvoiceVO create(@RequestParam(value = "order_id")  Long orderId) {
        return this.orderQueryManager.getInvoice(orderId);
    }


}
