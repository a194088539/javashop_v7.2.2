package com.enation.app.javashop.api.manager.promotion;

import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeCat;
import com.enation.app.javashop.service.promotion.exchange.ExchangeCatManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * 积分分类控制器
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-29 16:56:22
 */
@RestController
@RequestMapping("/admin/promotion/exchange-cats")
@Api(description = "积分分类相关API")
@Validated
public class ExchangeCatManagerController {

    @Autowired
    private ExchangeCatManager exchangeCatManager;


    @ApiOperation(value = "查询某分类下的子分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parent_id", value = "父id，顶级为0", required = true, dataType = "int", paramType = "path")
    })
    @GetMapping(value = "/{parent_id}/children")
    public List list(@ApiIgnore @PathVariable("parent_id") Long parentId) {
        return this.exchangeCatManager.list(parentId);
    }


    @ApiOperation(value = "添加积分兑换分类", response = ExchangeCat.class)
    @PostMapping
    public ExchangeCat add(@Valid ExchangeCat exchangeCat) {

        this.exchangeCatManager.add(exchangeCat);

        return exchangeCat;
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改积分兑换分类", response = ExchangeCat.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    })
    public ExchangeCat edit(@Valid ExchangeCat exchangeCat, @PathVariable Long id) {

        this.exchangeCatManager.edit(exchangeCat, id);

        return exchangeCat;
    }


    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除积分兑换分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的积分兑换分类主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {

        this.exchangeCatManager.delete(id);

        return "";
    }


    @GetMapping(value = "/{id}")
    @ApiOperation(value = "查询一个积分兑换分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要查询的积分兑换分类主键", required = true, dataType = "int", paramType = "path")
    })
    public ExchangeCat get(@PathVariable Long id) {

        ExchangeCat exchangeCat = this.exchangeCatManager.getModel(id);

        return exchangeCat;
    }

}
