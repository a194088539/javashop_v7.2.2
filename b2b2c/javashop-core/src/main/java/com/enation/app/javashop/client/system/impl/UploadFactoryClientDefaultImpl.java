package com.enation.app.javashop.client.system.impl;

import com.enation.app.javashop.model.base.dto.FileDTO;
import com.enation.app.javashop.model.base.vo.FileVO;
import com.enation.app.javashop.service.base.plugin.upload.Uploader;
import com.enation.app.javashop.client.system.UploadFactoryClient;
import com.enation.app.javashop.service.base.service.FileManager;
import com.enation.app.javashop.service.base.service.impl.FileManagerImpl;
import com.enation.app.javashop.service.system.factory.UploadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @version v7.0
 * @Description:
 * @Author: zjp
 * @Date: 2018/7/27 16:27
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class UploadFactoryClientDefaultImpl implements UploadFactoryClient {
    @Autowired
    private UploadFactory uploadFactory;

    @Autowired
    private FileManager fileManager;

    @Override
    public String getUrl(String url, Integer width, Integer height) {
        Uploader uploader = uploadFactory.getUploader();
        return uploader.getThumbnailUrl(url, width, height);
    }

    @Override
    public FileVO upload(FileDTO input, String scene) {
        return fileManager.upload(input, scene);
    }
}
