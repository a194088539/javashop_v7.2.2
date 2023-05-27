package com.enation.app.javashop.client.system;

import com.enation.app.javashop.model.base.dto.FileDTO;
import com.enation.app.javashop.model.base.vo.FileVO;

/**
 * @version v7.0
 * @Description: 存储方案Client
 * @Author: zjp
 * @Date: 2018/7/27 16:26
 */
public interface UploadFactoryClient {
    /**
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
     String getUrl(String url, Integer width, Integer height);

    /**
     * 文件上传
     * @param input 文件
     * @param scene	业务类型
     * @return
     */
    FileVO upload(FileDTO input, String scene);
}
