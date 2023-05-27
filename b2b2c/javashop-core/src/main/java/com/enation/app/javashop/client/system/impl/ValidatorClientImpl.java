package com.enation.app.javashop.client.system.impl;

import com.enation.app.javashop.service.base.service.ValidatorManager;
import com.enation.app.javashop.client.system.ValidatorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 验证方式客户端
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.6
 * 2019-12-23
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class ValidatorClientImpl implements ValidatorClient {

    @Autowired
    private ValidatorManager validatorManager;

    @Override
    public void validate() {
        this.validatorManager.validate();
    }
}
