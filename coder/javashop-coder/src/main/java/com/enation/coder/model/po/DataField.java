package com.enation.coder.model.po;

import java.io.Serializable;

import com.enation.coder.model.enums.FiledDataType;
import com.enation.framework.database.NotDbField;

/**
 * 字段实体
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 下午2:09:50
 */
public class DataField implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4296815557987838138L;

	private Integer field_id;
	
	private String english_name;
	
	private String china_name;
	
	private String data_type;
	
	private String data_size;
	
	private Long add_time;
	
	private Integer is_primary;
	
	private String validate_items;//校验内容
	
	private Integer model_id;
	
	private Integer status;//删除状态  0 删除   1未删除
	
	private String data_type_text;

	private Integer version_id;

	private Integer project_id;

	private String table_name;


	public Integer getField_id() {
		return field_id;
	}

	public void setField_id(Integer field_id) {
		this.field_id = field_id;
	}

	public String getEnglish_name() {
		return english_name;
	}

	public void setEnglish_name(String english_name) {
		this.english_name = english_name;
	}

	public String getChina_name() {
		return china_name;
	}

	public void setChina_name(String china_name) {
		this.china_name = china_name;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getData_size() {
		return data_size;
	}

	public void setData_size(String data_size) {
		this.data_size = data_size;
	}

	public Long getAdd_time() {
		return add_time;
	}

	public void setAdd_time(Long add_time) {
		this.add_time = add_time;
	}

	public Integer getIs_primary() {
		return is_primary;
	}

	public void setIs_primary(Integer is_primary) {
		this.is_primary = is_primary;
	}

	public Integer getModel_id() {
		return model_id;
	}

	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


	public String getValidate_items() {
		return validate_items;
	}

	public void setValidate_items(String validate_items) {
		this.validate_items = validate_items;
	}

	@NotDbField
	public String getData_type_text() {
		if(this.data_type!=null){
			return FiledDataType.valueOf(data_type).description();
		}
		return data_type_text;
	}

	public void setData_type_text(String data_type_text) {
		this.data_type_text = data_type_text;
	}


	public Integer getVersion_id() {
		return version_id;
	}

	public void setVersion_id(Integer version_id) {
		this.version_id = version_id;
	}

	public Integer getProject_id() {
		return project_id;
	}

	public void setProject_id(Integer project_id) {
		this.project_id = project_id;
	}

	public String getTable_name() {
		return table_name;
	}

	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
}
