package com.enation.app.javashop.framework.logs.appender;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Description : 日志对象类
 * @Author snow
 * @Date: 2020-01-09 16:03
 * @Version v1.0
 */
@Document(indexName="log-index-#{ T(com.enation.app.javashop.framework.logs.appender.IndexNameConfig).getDateStr() }", type="log")
@Configuration
public class EsLog implements Serializable {
	private static final long serialVersionUID = 4902288107813352023L;
	
	@Id
	private String id;
	
	@Field(type=FieldType.keyword, store=false)
	private String level;
	
	@Field(type=FieldType.keyword, store=false)
	private String threadName;
	
	@Field(type=FieldType.keyword, store=false)
	private String loggerName;
	
	@Field(type=FieldType.keyword, store=false)
	private String message;
	
	@Field(type = FieldType.Date,store = false)
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
	private Date logTime;

	@Field(type=FieldType.keyword, store=false)
	private String appName;

	@Field(type=FieldType.keyword, store=false)
	private String instance;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Date getLogTime() {
		return logTime;
	}

	public void setLogTime(Date logTime) {
		this.logTime = logTime;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	@Override
	public String toString() {
		return "EsLog{" +
				"id='" + id + '\'' +
				", level='" + level + '\'' +
				", threadName='" + threadName + '\'' +
				", loggerName='" + loggerName + '\'' +
				", message='" + message + '\'' +
				", logTime=" + logTime +
				", appName='" + appName + '\'' +
				", instance='" + instance + '\'' +
				'}';
	}
}