package com.enation.coder.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 文件配置信息
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018年3月13日 上午10:40:05
 */
@Component
public class FileConfig {

	@Value("${file.path}")
    private String filePath;
	
	@Value("${file.zip.path}")
	private String zipFilePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}
	
	
}
