package com.enation.app.javashop.api.manager.goods;

import com.enation.app.javashop.model.goods.dos.SpecValuesDO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.goods.SpecValuesManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 规格值控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 10:23:53
 */
@RestController
@RequestMapping("/admin/goods/specs/{spec_id}/values")
@Api(description = "规格值相关API")
@Validated
public class SpecValuesManagerController {

    @Autowired
    private SpecValuesManager specValuesManager;

    @ApiOperation(value = "查询规格值列表", response = SpecValuesDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spec_id", value = "规格id", required = true, dataType = "int", paramType = "path"),})
    @GetMapping
    public List<SpecValuesDO> list(@PathVariable("spec_id") Long specId) {
        return this.specValuesManager.listBySpecId(specId, Permission.ADMIN);
    }

    @ApiOperation(value = "添加某规格的规格值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spec_id", value = "规格id", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name = "value_list", value = "规格值集合", required = false, dataType = "string", paramType = "query", allowMultiple = true),})
    @PostMapping
    public List<SpecValuesDO> saveSpecValue(@PathVariable("spec_id") Long specId, @NotNull(message = "至少添加一个规格值") @ApiIgnore @RequestParam(value = "value_list", required = false) String[] valueList) {

        return this.specValuesManager.saveSpecValue(specId, valueList);
    }

}
