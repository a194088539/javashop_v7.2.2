package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;

/**
 * 数据库操作运行期异常
 * @author kingapex
 * 2010-1-6下午06:16:47
 */
public class DBRuntimeException extends RuntimeException {

 
	private static final long serialVersionUID = -368646349014580765L;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	public DBRuntimeException(String message){
		super(message);
	}
	public DBRuntimeException(Exception e,String sql) {
		
		super("数据库运行期异常");
		e.printStackTrace();
		logger.error("数据库运行期异常，相关sql语句为"+ sql);
	}
	
	
	public DBRuntimeException(String message,String sql) {
		
		super(message);
		logger.error(message+"，相关sql语句为"+ sql);
	}
	
	
}
