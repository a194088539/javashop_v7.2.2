package com.enation.coder.model.vo;

import java.io.Serializable;

/**
 * 代码模板需要的部分参数
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月12日 下午4:44:46
 */
public class CoderParam implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8873004075390537625L;

	private Integer modelId;
	
	private String packageName;
	
	private String username;

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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
}
