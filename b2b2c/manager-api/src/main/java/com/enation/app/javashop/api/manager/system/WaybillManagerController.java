package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.model.system.dos.UploaderDO;
import com.enation.app.javashop.model.system.dos.WayBillDO;
import com.enation.app.javashop.model.system.vo.WayBillVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.enation.app.javashop.framework.database.WebPage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import com.enation.app.javashop.service.system.WaybillManager;

/**
 * 电子面单控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-06-08 16:26:05
 */
@RestController
@RequestMapping("/admin/systems/waybills")
@Api(description = "电子面单相关API")
public class WaybillManagerController {

    @Autowired
    private WaybillManager waybillManager;


    @ApiOperation(value = "查询电子面单列表", response = WayBillDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {
        return this.waybillManager.list(pageNo, pageSize);
    }

    @ApiOperation(value = "修改电子面单", response = UploaderDO.class)
    @PutMapping(value = "/{bean}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bean", value = "电子面单bean id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "wayBillVO", value = "电子面单对象", required = true, dataType = "WayBillVO", paramType = "body")
    })
    public WayBillVO edit(@PathVariable String bean, @RequestBody @ApiIgnore WayBillVO wayBillVO) {
        wayBillVO.setBean(bean);
        return this.waybillManager.edit(wayBillVO);
    }


    @PutMapping(value = "/{bean}/open")
    @ApiOperation(value = "开启电子面单", response = WayBillDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bean", value = "bean", required = true, dataType = "String", paramType = "path")
    })
    public String open(@PathVariable String bean) {
        this.waybillManager.open(bean);
        return null;
    }

    @ApiOperation(value = "获取电子面单配置", response = String.class)
    @GetMapping("/{bean}")
    @ApiImplicitParam(name = "bean", value = "电子面单bean id", required = true, dataType = "String", paramType = "path")
    public WayBillVO getWaybillSetting(@PathVariable String bean) {
        return waybillManager.getWaybillConfig(bean);
    }

}
