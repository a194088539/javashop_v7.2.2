package com.enation.coder.model.po;

import java.io.Serializable;

/**
 * 项目的模块
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月21日 下午2:06:38
 */
public class Module implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7319518770711399902L;

	private Integer module_id;
	
	private String module_name;
	
	private Long add_time;
	
	private Integer project_id;

	private Integer version_id;

	public Integer getModule_id() {
		return module_id;
	}

	public void setModule_id(Integer module_id) {
		this.module_id = module_id;
	}

	public String getModule_name() {
		return module_name;
	}

	public void setModule_name(String module_name) {
		this.module_name = module_name;
	}

	public Long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(Long add_time) {
		this.add_time = add_time;
	}

	public Integer getProject_id() {
		return project_id;
	}

	public void setProject_id(Integer project_id) {
		this.project_id = project_id;
	}

	public Integer getVersion_id() {
		return version_id;
	}

	public void setVersion_id(Integer version_id) {
		this.version_id = version_id;
	}
}
