package com.enation.coder.model.enums;

/**
 * 
 * 权限
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月9日 下午4:08:27
 */
public enum PowerType {

	
	seller,manager,buyer,core;
	
	PowerType(){
	}

	public String value(){
		return this.name();
	}
}
