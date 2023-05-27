package com.enation.coder.model.vo;

import java.io.Serializable;

/**
 * 生成文件使用参数
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月12日 下午4:36:49
 */
public class GenerateParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8374552198669406911L;

	private String[] generateItems;//生成项 controller /service
	
	private Integer modelId;
	
	private String packageName;

	public String[] getGenerateItems() {
		return generateItems;
	}

	public void setGenerateItems(String[] generateItems) {
		this.generateItems = generateItems;
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	
}
