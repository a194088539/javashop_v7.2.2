package com.enation.coder.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 * @author xulipeng
 * 2018年03月18日14:06:48
 */
public interface IFileManager {

	/**
	 * 读取表
	 * @return
	 */
	public List<Map> getTables(MultipartFile file);
	
	/**
	 * 创建模型
	 * @param code
	 */
	public void createModel(String[] code,Integer projectId,Integer modeuleId);
}
