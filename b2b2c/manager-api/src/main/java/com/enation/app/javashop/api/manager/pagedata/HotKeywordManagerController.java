package com.enation.app.javashop.api.manager.pagedata;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.enation.app.javashop.framework.database.WebPage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import com.enation.app.javashop.model.pagedata.HotKeyword;
import com.enation.app.javashop.service.pagedata.HotKeywordManager;

/**
 * 热门关键字控制器
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-04 10:43:23
 */
@RestController
@RequestMapping("/admin/pages/hot-keywords")
@Api(description = "热门关键字相关API")
public class HotKeywordManagerController {

    @Autowired
    private HotKeywordManager hotKeywordManager;


    @ApiOperation(value = "查询热门关键字列表", response = HotKeyword.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize) {

        return this.hotKeywordManager.list(pageNo, pageSize);
    }


    @ApiOperation(value = "添加热门关键字", response = HotKeyword.class)
    @PostMapping
    public HotKeyword add(@Valid HotKeyword hotKeyword) {

        this.hotKeywordManager.add(hotKeyword);

        return hotKeyword;
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改热门关键字", response = HotKeyword.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    })
    public HotKeyword edit(@Valid HotKeyword hotKeyword, @PathVariable Long id) {

        this.hotKeywordManager.edit(hotKeyword, id);

        return hotKeyword;
    }


    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除热门关键字")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的热门关键字主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {

        this.hotKeywordManager.delete(id);

        return "";
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个热门关键字")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的热门关键字主键", required = true, dataType = "int", paramType = "path")
    })
    public HotKeyword get(@PathVariable Long id) {

        HotKeyword hotKeyword = this.hotKeywordManager.getModel(id);

        return hotKeyword;
    }

}
