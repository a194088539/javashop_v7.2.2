package com.enation.app.javashop.api.manager.aftersale;

import com.enation.app.javashop.model.aftersale.dto.RefundQueryParam;
import com.enation.app.javashop.model.aftersale.enums.ServiceOperateTypeEnum;
import com.enation.app.javashop.model.aftersale.vo.RefundRecordVO;
import com.enation.app.javashop.service.aftersale.AfterSaleRefundManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * 售后退款相关API
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-28
 */
@Api(description="售后退款相关API")
@RestController
@RequestMapping("/admin/after-sales/refund")
@Validated
public class RefundManagerController {

    @Autowired
    private AfterSaleRefundManager afterSaleRefundManager;

    @ApiOperation(value = "获取售后退款单列表", response = RefundRecordVO.class)
    @GetMapping()
    public WebPage list(@Valid RefundQueryParam param){
        return afterSaleRefundManager.list(param);
    }


    @ApiOperation(value = "平台退款操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service_sn", value = "售后退款单号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "refund_price", value = "退款金额", required = true, dataType = "Double", paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "售后退款备注", dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/{service_sn}")
    public void refund(@PathVariable("service_sn") @ApiIgnore String serviceSn, @ApiIgnore Double refundPrice, @ApiIgnore String remark){
        this.afterSaleRefundManager.adminRefund(serviceSn, refundPrice, remark, ServiceOperateTypeEnum.ADMIN_REFUND);
    }

    @ApiOperation(value = "退款单导出Excel")
    @GetMapping(value = "/export")
    public List<RefundRecordVO> excel(@Valid RefundQueryParam param) {

        return afterSaleRefundManager.exportExcel(param);
    }
}
