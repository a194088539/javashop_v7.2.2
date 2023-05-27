package com.enation.app.javashop.service.base.service;

import com.enation.app.javashop.model.base.dto.FileDTO;
import com.enation.app.javashop.model.base.vo.FileVO;
/**
 * 文件上传接口
 * @author zh
 * @version v2.0
 * @since v7.0
 * 2018年3月19日 下午4:37:44
 */
public interface FileManager {
	/**
	 * 文件上传
	 * @param input 文件
	 * @param scene	业务类型
	 * @return
	 */
	 FileVO upload(FileDTO input,String scene);
	/**
	 * 删除文件
	 * @param filePath	文件路径
	 */
	 void deleteFile(String filePath);


}
