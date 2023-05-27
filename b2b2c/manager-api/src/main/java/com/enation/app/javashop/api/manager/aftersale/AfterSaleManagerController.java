package com.enation.app.javashop.api.manager.aftersale;

import com.enation.app.javashop.model.aftersale.dto.AfterSaleQueryParam;
import com.enation.app.javashop.model.aftersale.vo.AfterSaleExportVO;
import com.enation.app.javashop.model.aftersale.vo.AfterSaleRecordVO;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.aftersale.AfterSaleQueryManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 售后服务相关API
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-24
 */
@Api(description = "售后服务相关API")
@RestController
@RequestMapping("/admin/after-sales")
@Validated
public class AfterSaleManagerController {

    @Autowired
    private AfterSaleQueryManager afterSaleQueryManager;

    @ApiOperation(value = "获取申请售后服务记录列表", response = AfterSaleRecordVO.class)
    @GetMapping()
    public WebPage list(@Valid AfterSaleQueryParam param){
        return afterSaleQueryManager.list(param);
    }

    @ApiOperation(value = "获取售后服务详细信息", response = ApplyAfterSaleVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "service_sn", value = "售后服务单号", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping(value = "/detail/{service_sn}")
    public ApplyAfterSaleVO detail(@PathVariable("service_sn") String serviceSn){

        return afterSaleQueryManager.detail(serviceSn, Permission.ADMIN);
    }

    @ApiOperation(value = "导出售后服务信息", response = AfterSaleExportVO.class)
    @GetMapping(value = "/export")
    public List<AfterSaleExportVO> export(@Valid AfterSaleQueryParam param){
        return afterSaleQueryManager.exportAfterSale(param);
    }
}
