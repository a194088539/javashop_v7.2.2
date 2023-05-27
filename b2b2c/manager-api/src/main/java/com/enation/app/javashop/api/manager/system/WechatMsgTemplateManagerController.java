package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.model.system.dos.WechatMsgTemplate;
import com.enation.app.javashop.service.system.WechatMsgTemplateManager;
import com.enation.app.javashop.framework.database.WebPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 微信服务消息模板控制器
 *
 * @author fk
 * @version v7.1.4
 * @since vv7.1.0
 * 2019-06-14 16:42:35
 */
@RestController
@RequestMapping("/admin/systems/wechat-msg-tmp")
@Api(description = "微信服务消息模板相关API")
public class WechatMsgTemplateManagerController {

    @Autowired
    private WechatMsgTemplateManager wechatMsgTemplateManager;


    @ApiOperation(value = "查询微信服务消息模板列表", response = WechatMsgTemplate.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {

        return this.wechatMsgTemplateManager.list(pageNo, pageSize);
    }

    @ApiOperation(value = "查询微信服务消息模板是否已经同步")
    @GetMapping("/sync")
    public boolean sync(){

        return wechatMsgTemplateManager.isSycn();
    }

    @ApiOperation(value = "同步微信服务消息模板")
    @PostMapping("/sync")
    public String syncMsgTmp(){

        wechatMsgTemplateManager.sycn();

        return "";
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个微信服务消息模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的微信服务消息模板主键", required = true, dataType = "int", paramType = "path")
    })
    public WechatMsgTemplate get(@PathVariable Long id) {

        WechatMsgTemplate wechatMsgTemplate = this.wechatMsgTemplateManager.getModel(id);

        return wechatMsgTemplate;
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改微信服务消息模板", response = WechatMsgTemplate.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    })
    public WechatMsgTemplate edit(@Valid WechatMsgTemplate wechatMsgTemplate, @PathVariable Long id) {

        this.wechatMsgTemplateManager.edit(wechatMsgTemplate, id);

        return wechatMsgTemplate;
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除微信服务消息模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的微信服务消息模板主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {

        this.wechatMsgTemplateManager.delete(id);

        return "";
    }




}
