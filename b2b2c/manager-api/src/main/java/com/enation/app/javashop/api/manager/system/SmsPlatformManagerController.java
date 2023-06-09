package com.enation.app.javashop.api.manager.system;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.enation.app.javashop.model.system.dos.SmsPlatformDO;
import com.enation.app.javashop.model.system.vo.SmsPlatformVO;
import com.enation.app.javashop.service.system.SmsPlatformManager;
import com.enation.app.javashop.framework.database.WebPage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;


/**
 * 短信网关表控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 11:31:05
 */
@RestController
@RequestMapping("/admin/systems/platforms")
@Api(description = "短信网关相关API")
public class SmsPlatformManagerController {

    @Autowired
    private SmsPlatformManager smsPlatformManager;


    @ApiOperation(value = "查询短信网关列表", response = SmsPlatformDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore @NotEmpty(message = "页码不能为空") Long pageNo, @ApiIgnore @NotEmpty(message = "每页数量不能为空") Long pageSize) {
        return this.smsPlatformManager.list(pageNo, pageSize);
    }


    @ApiOperation(value = "修改短信网关", response = SmsPlatformDO.class)
    @PutMapping(value = "/{bean}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bean", value = "短信网关bean id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "sms_platform", value = "短信网关对象", required = true, dataType = "SmsPlatformVO", paramType = "body")
    })
    public SmsPlatformVO edit(@PathVariable String bean, @RequestBody @ApiIgnore SmsPlatformVO smsPlatformVO) {
        smsPlatformVO.setBean(bean);
        return this.smsPlatformManager.edit(smsPlatformVO);
    }


    @GetMapping(value = "/{bean}")
    @ApiOperation(value = "查询一个短信网关参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bean", value = "短信网关bean id", required = true, dataType = "String", paramType = "path")
    })
    public SmsPlatformVO getConfig(@PathVariable String bean) {
        return this.smsPlatformManager.getConfig(bean);
    }

    @ApiOperation(value = "开启某个短信网关", response = String.class)
    @PutMapping("/{bean}/open")
    @ApiImplicitParam(name = "bean", value = "bean", required = true, dataType = "String", paramType = "path")
    public String open(@PathVariable String bean) {
        this.smsPlatformManager.openPlatform(bean);
        return null;
    }

}
