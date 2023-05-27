package com.enation.coder.model.enums;

/**
 * 
 * 字段数据类型
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 下午4:08:27
 */
public enum FiledDataType {

	
	VARCHAR("字符串","String"),
	INT("整数","Integer"),
	DECIMAL("浮点型","Double"),
	LONGTEXT("大文本","String"),
	BIGINT("长整型","Long"),
	SMALLINT("短整型","Integer");
	
	private String description;
	
	private String type;

	FiledDataType(String _description,String _type){
		  this.description = _description;
		  this.type = _type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String description(){
		return this.description;
	}
	
	public String value(){
		return this.name();
	}
}
