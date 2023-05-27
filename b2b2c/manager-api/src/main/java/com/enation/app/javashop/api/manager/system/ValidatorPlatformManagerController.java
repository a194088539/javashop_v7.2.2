package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.system.dos.ValidatorPlatformDO;
import com.enation.app.javashop.model.system.vo.ValidatorPlatformVO;
import com.enation.app.javashop.service.system.ValidatorPlatformManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 滑块验证平台相关API
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-18
 */
@Api(description = "滑块验证平台相关API")
@RestController
@RequestMapping("/admin/systems/validator")
@Validated
public class ValidatorPlatformManagerController {

    @Autowired
    private ValidatorPlatformManager validatorPlatformManager;

    @ApiOperation(value = "查询验证平台列表", response = ValidatorPlatformDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {

        return this.validatorPlatformManager.list(pageNo, pageSize);
    }

    @ApiOperation(value = "修改验证平台信息", response = ValidatorPlatformDO.class)
    @PutMapping(value = "/{pluginId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "plugin_id", value = "验证平台插件ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "slider_verify_platform", value = "验证平台对象", required = true, dataType = "ValidatorPlatformVO", paramType = "body")
    })
    public ValidatorPlatformVO edit(@PathVariable String pluginId, @RequestBody @ApiIgnore ValidatorPlatformVO validatorPlatformVO) {
        validatorPlatformVO.setPluginId(pluginId);
        return this.validatorPlatformManager.edit(validatorPlatformVO);
    }

    @ApiOperation(value = "获取验证平台的配置", response = String.class)
    @GetMapping("/{pluginId}")
    @ApiImplicitParam(name = "plugin_id", value = "验证平台插件ID", required = true, dataType = "String", paramType = "path")
    public ValidatorPlatformVO getUploadSetting(@PathVariable String pluginId) {
        return this.validatorPlatformManager.getConfig(pluginId);
    }

    @ApiOperation(value = "开启某个验证平台", response = String.class)
    @PutMapping("/{pluginId}/open")
    @ApiImplicitParam(name = "plugin_id", value = "验证平台插件ID", required = true, dataType = "String", paramType = "path")
    public String open(@PathVariable String pluginId) {
        this.validatorPlatformManager.open(pluginId);
        return null;
    }
}
