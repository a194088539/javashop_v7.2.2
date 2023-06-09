package com.enation.app.javashop.api.manager.goods;

import com.enation.app.javashop.model.support.validator.annotation.SortType;
import com.enation.app.javashop.model.goods.dos.ParametersDO;
import com.enation.app.javashop.service.goods.ParametersManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 参数控制器
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 16:14:31
 */
@RestController
@RequestMapping("/admin/goods/parameters")
@Api(description = "参数相关API")
@Validated
public class ParametersManagerController {

	@Autowired
	private ParametersManager parametersManager;

	@ApiOperation(value = "添加参数", response = ParametersDO.class)
	@PostMapping
	public ParametersDO add(@Valid ParametersDO parameters) {

		this.parametersManager.add(parameters);

		return parameters;
	}

	@PutMapping(value = "/{id}")
	@ApiOperation(value = "修改参数", response = ParametersDO.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path") })
	public ParametersDO edit(@Valid ParametersDO parameters, @PathVariable Long id) {

		this.parametersManager.edit(parameters, id);

		return parameters;
	}

	@DeleteMapping(value = "/{id}")
	@ApiOperation(value = "删除参数")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "要删除的参数主键", required = true, dataType = "int", paramType = "path") })
	public String delete(@PathVariable Long id) {

		this.parametersManager.delete(id);

		return "";
	}

	@GetMapping(value = "/{id}")
	@ApiOperation(value = "查询一个参数")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "要查询的参数主键", required = true, dataType = "int", paramType = "path") })
	public ParametersDO get(@PathVariable Long id) {

		ParametersDO parameters = this.parametersManager.getModel(id);

		return parameters;
	}

	@ApiOperation(value = "参数上移或者下移", notes = "分类绑定参数时，上移下移参数组时使用")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sort_type", value = "排序类型，上移 up，下移down", required = true, paramType = "query", dataType = "String"),
			@ApiImplicitParam(name = "param_id", value = "参数主键", required = true, paramType = "path", dataType = "String"), })
	@PutMapping(value = "/{param_id}/sort")
	public String paramSort(@PathVariable("param_id") Long paramId, @ApiIgnore @SortType String sortType) {

		this.parametersManager.paramSort(paramId, sortType);

		return null;
	}

}
