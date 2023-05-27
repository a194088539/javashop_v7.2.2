package com.enation.coder.model.po;

import java.io.Serializable;

import com.enation.framework.database.NotDbField;
import com.enation.framework.util.DateUtil;

/**
 * 
 * 项目
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月8日 下午5:49:56
 */
public class Project implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4599796991808881371L;
	
	private Integer id;	//主键
	private String project_name;//项目名称
	private String package_name;//包名
	private Integer user_id;//管理员id
	private Long add_time;//添加时间
	private String add_time_text;//添加时间标准格式
	private String since;//产品版本号
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getPackage_name() {
		return package_name;
	}
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Long getAdd_time() {
		return add_time;
	}
	public void setAdd_time(Long add_time) {
		this.add_time = add_time;
	}
	
	@NotDbField
	public String getAdd_time_text() {
		
		if(add_time!=null){
			return DateUtil.toString(add_time, "yyyy-MM-dd HH:mm:ss");
		}
		
		return add_time_text;
	}
	public void setAdd_time_text(String add_time_text) {
		this.add_time_text = add_time_text;
	}
	public String getSince() {
		return since;
	}
	public void setSince(String since) {
		this.since = since;
	}

	
	
}
