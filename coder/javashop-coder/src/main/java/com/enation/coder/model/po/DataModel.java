package com.enation.coder.model.po;

import java.io.Serializable;

/**
 * 模型类实体
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 上午11:37:51
 */
public class DataModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6883326655816155303L;

	private Integer model_id;//主键
	
	private String model_name;//模型名称
	
	private String english_name;//英文名
	
	private Long add_time;//添加时间
	
	private String table_name;//表名
	
	private Integer project_id;//项目id
	
	private String child_package_name;//总包名下的子包名
	
	private String version;//模型版本号
	
	private Integer module_id;//模块id

	/**
	 * 模块名称
	 */
	private String module_name;

	/**
	 * 项目版本号
	 */
	private Integer version_id;
	
	public Integer getModel_id() {
		return model_id;
	}

	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}

	public String getModel_name() {
		return model_name;
	}

	public void setModel_name(String model_name) {
		this.model_name = model_name;
	}

	public String getEnglish_name() {
		return english_name;
	}

	public void setEnglish_name(String english_name) {
		this.english_name = english_name;
	}

	public Long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(Long add_time) {
		this.add_time = add_time;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}

	public Integer getProject_id() {
		return project_id;
	}

	public void setProject_id(Integer project_id) {
		this.project_id = project_id;
	}

	public String getChild_package_name() {
		return child_package_name;
	}

	public void setChild_package_name(String child_package_name) {
		this.child_package_name = child_package_name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

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

	public Integer getVersion_id() {
		return version_id;
	}

	public void setVersion_id(Integer version_id) {
		this.version_id = version_id;
	}
}
