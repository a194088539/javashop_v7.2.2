package com.enation.app.javashop.api.seller.orderbill;

import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.orderbill.validator.annotation.BillItemType;
import com.enation.app.javashop.model.orderbill.dos.Bill;
import com.enation.app.javashop.model.orderbill.vo.BillDetail;
import com.enation.app.javashop.model.orderbill.vo.BillExcel;
import com.enation.app.javashop.model.orderbill.vo.BillQueryParam;
import com.enation.app.javashop.service.orderbill.BillItemManager;
import com.enation.app.javashop.service.orderbill.BillManager;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.security.model.Seller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author fk
 * @version v1.0
 * @Description: 结算单账单控制器
 * @date 2018/4/27 11:49
 * @since v7.0.0
 */
@RestController
@RequestMapping("/seller/order/bills")
@Api(description = "结算相关API")
@Validated
public class OrderBillSellerController {

    @Autowired
    private BillManager billManager;
    @Autowired
    private BillItemManager billItemManager;

    @ApiOperation(value = "商家查看我的账单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页数量", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "bill_sn", value = "结算编号", required = false, dataType = "String", paramType = "query")
    })
    @GetMapping
    public WebPage queryBillList(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore String billSn) {

        Seller seller = UserContext.getSeller();
        BillQueryParam param = new BillQueryParam();
        param.setPageNo(pageNo);
        param.setPageSize(pageSize);
        param.setSellerId(seller.getSellerId());
        param.setBillSn(billSn);

        return this.billManager.queryBills(param);
    }

    @ApiOperation(value = "商家查看某账单详细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bill_id", value = "结算单id", required = true, dataType = "int", paramType = "path"),
    })
    @GetMapping("/{bill_id}")
    public BillDetail queryBill(@PathVariable("bill_id") Long billId) {

        return this.billManager.getBillDetail(billId, Permission.SELLER);
    }


    @ApiOperation(value = "卖家对账单进行下一步操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bill_id", value = "账单id", required = true, dataType = "int", paramType = "path"),
    })
    @PutMapping(value = "/{bill_id}/next")
    public Bill nextBill(@PathVariable("bill_id") Long billId) {

        Bill bill = this.billManager.editStatus(billId, Permission.SELLER);

        return bill;
    }

    @ApiOperation(value = "查看账单中的订单列表或者退款单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页数量", required = false, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "bill_id", value = "账单id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "bill_type", value = "账单类型", required = true, dataType = "string", paramType = "path", allowableValues = "REFUND,PAYMENT"),
    })
    @GetMapping("/{bill_id}/{bill_type}")
    public WebPage queryBillItems(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @PathVariable("bill_id") Long billId, @BillItemType @PathVariable("bill_type") String billType) {

        return this.billItemManager.list(pageNo, pageSize, billId, billType);
    }

    @ApiOperation(value = "导出某账单详细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bill_id", value = "结算单id", required = true, dataType = "int", paramType = "path"),
    })
    @GetMapping("/{bill_id}/export")
    public BillExcel exportBill(@PathVariable("bill_id") Long billId) {

        return  this.billManager.exportBill(billId);
    }

}
