package com.enation.app.javashop.api.seller.system;

import com.enation.app.javashop.client.member.ShopLogisticsCompanyClient;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.model.shop.dos.ShopLogisticsSetting;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.service.system.LogisticsCompanyManager;
import com.enation.app.javashop.service.system.WaybillManager;
import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.order.vo.DeliveryVO;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 电子面单生成API
 *
 * @author zh
 * @version v7.0
 * @date 18/6/11 下午4:56
 * @since v7.0
 */

@RestController
@RequestMapping("/seller/waybill")
@Api(description = "电子面单生成api")
@Validated
public class WayBillSellerController {

    @Autowired
    private WaybillManager waybillManager;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private ShopLogisticsCompanyClient shopLogisticsCompanyClient;

    @Autowired
    private LogisticsCompanyManager logisticsCompanyManager;

    @ApiOperation(value = "电子面单生成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_sn", value = "订单编号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "logistics_id", value = "物流公司id", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public String create(@RequestParam(value = "order_sn") @NotEmpty(message = "订单编号不能为空") String orderSn, @RequestParam(value = "logistics_id") @NotNull(message = "物流公司id不能为空") Long logisticsId) {
        String result = this.waybillManager.createPrintData(orderSn, logisticsId);
        //获取电子面单的快递单号
        JSONObject resultJson = JSONObject.fromObject(result);
        if (resultJson.get("Success").equals(false)) {
            throw new ServiceException(SystemErrorCode.E911.code(), resultJson.get("Reason").toString());
        }

        Object order = resultJson.get("Order");
        JSONObject orders = JSONObject.fromObject(order);
        Seller seller = UserContext.getSeller();
        DeliveryVO delivery = new DeliveryVO();
        delivery.setDeliveryNo((String) orders.get("LogisticCode"));
        delivery.setOrderSn(orderSn);
        ShopLogisticsSetting shopLogisticsSetting = shopLogisticsCompanyClient.query(logisticsId, UserContext.getSeller().getSellerId());

        if (shopLogisticsSetting == null) {
            throw new ServiceException(SystemErrorCode.E912.code(), "电子面单未开启");
        }

        delivery.setLogiId(shopLogisticsSetting.getLogisticsId());
        delivery.setLogiName(logisticsCompanyManager.getModel(shopLogisticsSetting.getLogisticsId()).getName());
        delivery.setOperator("店铺:" + seller.getSellerName());
        orderClient.ship(delivery, OrderPermission.seller);
        String template = StringUtil.toString(resultJson.get("PrintTemplate"));
        return template;
    }


    @ApiOperation(value = "电子面单批量生成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_sns", value = "订单编号", required = true, dataType = "String", paramType = "query", allowMultiple = true),
            @ApiImplicitParam(name = "logistics_id", value = "物流公司id", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("/creates")
    public String create(@RequestParam(value = "order_sns") @NotEmpty(message = "订单编号不能为空") String[] orderSns, @RequestParam(value = "logistics_id") @NotNull(message = "物流公司id不能为空") Long logisticsId) {

        ShopLogisticsSetting shopLogisticsSetting = shopLogisticsCompanyClient.query(logisticsId, UserContext.getSeller().getSellerId());

        if (shopLogisticsSetting == null) {
            throw new ServiceException(SystemErrorCode.E912.code(), "电子面单未开启");
        }
        List<String> templates = new ArrayList<>();
        for (String orderSn : orderSns) {

            String result = this.waybillManager.createPrintData(orderSn, logisticsId);
            //获取电子面单的快递单号
            JSONObject resultJson = JSONObject.fromObject(result);
            if (resultJson.get("Success").equals(false)) {
                throw new ServiceException(SystemErrorCode.E911.code(), resultJson.get("Reason").toString());
            }

            Object order = resultJson.get("Order");
            JSONObject orders = JSONObject.fromObject(order);
            Seller seller = UserContext.getSeller();
            DeliveryVO delivery = new DeliveryVO();
            delivery.setDeliveryNo((String) orders.get("LogisticCode"));
            delivery.setOrderSn(orderSn);

            delivery.setLogiId(shopLogisticsSetting.getLogisticsId());
            delivery.setLogiName(logisticsCompanyManager.getModel(shopLogisticsSetting.getLogisticsId()).getName());
            delivery.setOperator("店铺:" + seller.getSellerName());
            orderClient.ship(delivery, OrderPermission.seller);
            String template = StringUtil.toString(resultJson.get("PrintTemplate"));
            templates.add(template);
        }
        return StringUtil.listToString(templates, ",");
    }


    @ApiOperation(value = "电子面单再次显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_sn", value = "订单编号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "logistics_id", value = "物流公司id", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("/reshow")
    public String reshow(@RequestParam(value = "order_sn") @NotEmpty(message = "订单编号不能为空") String orderSn, @RequestParam(value = "logistics_id") @NotNull(message = "物流公司id不能为空") Long logisticsId) {
        String result = this.waybillManager.createPrintData(orderSn, logisticsId);
        //获取电子面单的快递单号
        JSONObject resultJson = JSONObject.fromObject(result);
        if (resultJson.get("Success").equals(false)) {
            throw new ServiceException(SystemErrorCode.E911.code(), resultJson.get("Reason").toString());
        }
        Object order = resultJson.get("Order");
        ShopLogisticsSetting shopLogisticsSetting = shopLogisticsCompanyClient.query(logisticsId, UserContext.getSeller().getSellerId());

        if (shopLogisticsSetting == null) {
            throw new ServiceException(SystemErrorCode.E912.code(), "电子面单未开启");
        }
        String template = StringUtil.toString(resultJson.get("PrintTemplate"));
        return template;
    }


}
