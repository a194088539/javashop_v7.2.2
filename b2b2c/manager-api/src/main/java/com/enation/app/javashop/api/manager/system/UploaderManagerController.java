package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.framework.database.WebPage;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;


import javax.validation.constraints.NotEmpty;

import com.enation.app.javashop.model.system.dos.UploaderDO;
import com.enation.app.javashop.model.system.vo.UploaderVO;
import com.enation.app.javashop.service.system.UploaderManager;

/**
 * 存储方案控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-22 09:31:56
 */
@RestController
@RequestMapping("/admin/systems/uploaders")
@Api(description = "存储方案相关API")
public class UploaderManagerController {

    @Autowired
    private UploaderManager uploaderManager;


    @ApiOperation(value = "查询存储方案列表", response = UploaderDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore @NotEmpty(message = "页码不能为空") Long pageNo, @ApiIgnore @NotEmpty(message = "每页数量不能为空") Long pageSize) {
        return this.uploaderManager.list(pageNo, pageSize);
    }

    @ApiOperation(value = "修改存储方案参数", response = UploaderDO.class)
    @PutMapping(value = "/{bean}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bean", value = "存储方案bean id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "uploader", value = "存储对象", required = true, dataType = "UploaderVO", paramType = "body")
    })
    public UploaderVO edit(@PathVariable String bean, @RequestBody @ApiIgnore UploaderVO uploader) {
        uploader.setBean(bean);
        return this.uploaderManager.edit(uploader);
    }

    @ApiOperation(value = "开启某个存储方案", response = String.class)
    @PutMapping("/{bean}/open")
    @ApiImplicitParam(name = "bean", value = "bean", required = true, dataType = "String", paramType = "path")
    public String open(@PathVariable String bean) {
        this.uploaderManager.openUploader(bean);
        return null;
    }


    @ApiOperation(value = "获取存储方案的配置", response = String.class)
    @GetMapping("/{bean}")
    @ApiImplicitParam(name = "bean", value = "存储方案bean id", required = true, dataType = "String", paramType = "path")
    public UploaderVO getUploadSetting(@PathVariable String bean) {
        return this.uploaderManager.getUploadConfig(bean);
    }


}
