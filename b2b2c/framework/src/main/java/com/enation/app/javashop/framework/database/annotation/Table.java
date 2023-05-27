package com.enation.app.javashop.framework.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识为数据库的表名
 * @author Snow
 */
@Target(ElementType.TYPE) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface Table {

	/**
	 * 数据库的表名
	 * @return
	 */
	String name();
}
