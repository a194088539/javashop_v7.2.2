package com.enation.app.javashop.api.manager.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.service.goods.BrandManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 品牌控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-16 16:32:46
 */
@RestController
@RequestMapping("/admin/goods/brands")
@Api(description = "品牌相关API")
public class BrandManagerController {
    @Autowired
    private BrandManager brandManager;

    @ApiOperation(value = "查询品牌列表", response = BrandDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "品牌名称", dataType = "string", paramType = "query")})
    @GetMapping
    public WebPage list(@ApiIgnore @NotEmpty(message = "页码不能为空") Long pageNo,
                        @ApiIgnore @NotEmpty(message = "每页数量不能为空") Long pageSize,
                        String name) {

        return this.brandManager.list(pageNo, pageSize,name);
    }

    @ApiOperation(value = "添加品牌", response = BrandDO.class)
    @PostMapping
    public BrandDO add(@Valid BrandDO brand) {

        this.brandManager.add(brand);

        return brand;
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改品牌", response = BrandDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")})
    public BrandDO edit(@Valid BrandDO brand, @PathVariable Long id) {

        this.brandManager.edit(brand, id);

        return brand;
    }

    @DeleteMapping(value = "/{ids}")
    @ApiOperation(value = "删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "要删除的品牌主键集合", required = true, dataType = "int", paramType = "path", allowMultiple = true)})
    public String delete(@PathVariable Long[] ids) {

        this.brandManager.delete(ids);

        return "";
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的品牌主键", required = true, dataType = "int", paramType = "path")})
    public BrandDO get(@PathVariable Long id) {

        BrandDO brand = this.brandManager.getModel(id);

        return brand;
    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "查询所有品牌")
    public List<BrandDO> listAll() {

        List<BrandDO> brands = this.brandManager.getAllBrands();

        return brands;
    }

}
