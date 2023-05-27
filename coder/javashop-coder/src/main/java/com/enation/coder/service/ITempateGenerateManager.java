package com.enation.coder.service;

import org.springframework.ui.Model;

import com.enation.coder.model.vo.CoderParam;

/**
 * 模板生成manager
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月12日 下午1:59:26
 */
public interface ITempateGenerateManager {

	
	/**
	 * 生成实体
	 * @param model 
	 * @param param
	 * @return
	 */
	Model generateModel(CoderParam param, Model model);
	
	/**
	 * 生成控制器
	 * @param param
	 * @param model 
	 * @return
	 */
	Model generateController(CoderParam param, Model model);
	
	/**
	 * 生成业务层
	 * @param param
	 * @param model 
	 * @return
	 */
	Model generateService(CoderParam param, Model model);

	/**
	 * 生成接口文件
	 * @param param
	 * @param model
	 */
	void generateInterfaceService(CoderParam param, Model model);

	/**
	 * 生成pdm模板
	 * @param project_id
	 * @param versionId
	 * @param model
	 */
    void generatePdm(int project_id, Integer versionId, Model model);
}
