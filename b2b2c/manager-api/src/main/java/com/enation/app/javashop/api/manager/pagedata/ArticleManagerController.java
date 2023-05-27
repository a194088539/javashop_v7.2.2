package com.enation.app.javashop.api.manager.pagedata;

import com.enation.app.javashop.model.pagedata.vo.ArticleDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.enation.app.javashop.framework.database.WebPage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import com.enation.app.javashop.model.pagedata.Article;
import com.enation.app.javashop.service.pagedata.ArticleManager;

/**
 * 文章控制器
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-12 10:43:18
 */
@RestController
@RequestMapping("/admin/pages/articles")
@Api(description = "文章相关API")
public class ArticleManagerController {

    @Autowired
    private ArticleManager articleManager;


    @ApiOperation(value = "查询文章列表", response = ArticleDetail.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "文章名称", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "category_id", value = "文章分类", dataType = "string", paramType = "query"),
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, String name, @ApiIgnore Long categoryId) {

        return this.articleManager.list(pageNo, pageSize, name, categoryId);
    }


    @ApiOperation(value = "添加文章", response = Article.class)
    @PostMapping
    public Article add(@Valid Article article) {

        this.articleManager.add(article);

        return article;
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改文章", response = Article.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    })
    public Article edit(@Valid Article article, @PathVariable Long id) {

        this.articleManager.edit(article, id);

        return article;
    }


    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的文章主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {

        this.articleManager.delete(id);

        return "";
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的文章主键", required = true, dataType = "int", paramType = "path")
    })
    public Article get(@PathVariable Long id) {

        Article article = this.articleManager.getModel(id);

        return article;
    }

}
