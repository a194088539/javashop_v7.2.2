package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.system.dos.MessageTemplateDO;
import com.enation.app.javashop.model.system.dto.MessageTemplateDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.enation.app.javashop.service.system.MessageTemplateManager;

/**
 * 消息模版控制器
 *
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-05 16:38:43
 */
@RestController
@RequestMapping("/admin/systems/message-templates")
@Api(description = "消息模版相关API")
@Validated
public class MessageTemplateManagerController {

    @Autowired
    private MessageTemplateManager messageTemplateManager;


    @ApiOperation(value = "查询消息模版列表", response = MessageTemplateDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "消息类型", required = true, dataType = "String", paramType = "query", allowableValues = "SHOP,MEMBER")
    })
    @GetMapping
    public WebPage list(@ApiIgnore @NotNull(message = "页码不能为空") Long pageNo, @ApiIgnore @NotNull(message = "每页数量不能为空") Long pageSize, @NotEmpty(message = "模版类型必填") String type) {

        return this.messageTemplateManager.list(pageNo, pageSize, type);
    }


    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改消息模版", response = MessageTemplateDO.class)
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    public MessageTemplateDO edit(@Valid MessageTemplateDTO messageTemplate, @PathVariable("id") Long id) {
        return this.messageTemplateManager.edit(messageTemplate, id);
    }

}
