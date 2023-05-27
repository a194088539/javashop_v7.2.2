package ${packageStr};

import com.enation.app.javashop.framework.database.Page;
${importStr}

/**
 * ${model.model_name}业务层
 * @author ${username}
 * @version v${version}
 * @since v${since}
 * ${time}
 */
public interface ${className}Manager	{

	/**
	 * 查询${model.model_name}列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return Page 
	 */
	Page list(int page,int pageSize);
	/**
	 * 添加${model.model_name}
	 * @param ${model.english_name} ${model.model_name}
	 * @return ${className} ${model.model_name}
	 */
	${className} add(${className} ${model.english_name});

	/**
	* 修改${model.model_name}
	* @param ${model.english_name} ${model.model_name}
	* @param id ${model.model_name}主键
	* @return ${className} ${model.model_name}
	*/
	${className} edit(${className} ${model.english_name},Integer id);
	
	/**
	 * 删除${model.model_name}
	 * @param id ${model.model_name}主键
	 */
	void delete(Integer id);
	
	/**
	 * 获取${model.model_name}
	 * @param id ${model.model_name}主键
	 * @return ${className}  ${model.model_name}
	 */
	${className} getModel(Integer id);

}