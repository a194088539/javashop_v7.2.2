package ${packageStr};

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.enation.app.javashop.framework.database.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
${importStr}

/**
 * ${model.model_name}控制器
 * @author ${username}
 * @version v${version}
 * @since v${since}
 * ${time}
 */
@RestController
@RequestMapping("${apiPath}")
@Api(description = "${model.model_name}相关API")
public class ${className}Controller	{
	
	@Autowired
	private	${className}Manager ${model.english_name}Manager;
				

	@ApiOperation(value	= "查询${model.model_name}列表", response = ${className}.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "page_no",	value =	"页码",	required = true, dataType = "int",	paramType =	"query"),
		 @ApiImplicitParam(name	= "page_size",	value =	"每页显示数量",	required = true, dataType = "int",	paramType =	"query")
	})
	@GetMapping
	public Page list(@ApiIgnore Integer pageNo,@ApiIgnore Integer pageSize)	{
		
		return	this.${model.english_name}Manager.list(pageNo,pageSize);
	}
	
	
	@ApiOperation(value	= "添加${model.model_name}", response = ${className}.class)
	@PostMapping
	public ${className} add(@Valid ${className} ${model.english_name})	{
		
		this.${model.english_name}Manager.add(${model.english_name});
		
		return	${model.english_name};
	}
				
	@PutMapping(value = "/{id}")
	@ApiOperation(value	= "修改${model.model_name}", response = ${className}.class)
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	${className} edit(@Valid ${className} ${model.english_name}, @PathVariable Integer id) {
		
		this.${model.english_name}Manager.edit(${model.english_name},id);
		
		return	${model.english_name};
	}
			
	
	@DeleteMapping(value = "/{id}")
	@ApiOperation(value	= "删除${model.model_name}")
	@ApiImplicitParams({
		 @ApiImplicitParam(name	= "id",	value =	"要删除的${model.model_name}主键",	required = true, dataType = "int",	paramType =	"path")
	})
	public	String	delete(@PathVariable Integer id) {
		
		this.${model.english_name}Manager.delete(id);
		
		return "";
	}
				
	
	@GetMapping(value =	"/{id}")
	@ApiOperation(value	= "查询一个${model.model_name}")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id",	value = "要查询的${model.model_name}主键",	required = true, dataType = "int",	paramType = "path")	
	})
	public	${className} get(@PathVariable	Integer	id)	{
		
		${className} ${model.english_name} = this.${model.english_name}Manager.getModel(id);
		
		return	${model.english_name};
	}
				
}