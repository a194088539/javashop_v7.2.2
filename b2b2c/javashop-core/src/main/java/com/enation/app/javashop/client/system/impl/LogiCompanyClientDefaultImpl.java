package com.enation.app.javashop.client.system.impl;

import com.enation.app.javashop.client.system.LogiCompanyClient;
import com.enation.app.javashop.model.system.dos.LogisticsCompanyDO;
import com.enation.app.javashop.service.system.LogisticsCompanyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @version v7.0
 * @Description:
 * @Author: zjp
 * @Date: 2018/7/26 14:18
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class LogiCompanyClientDefaultImpl implements LogiCompanyClient {

    @Autowired
    private LogisticsCompanyManager logisticsCompanyManager;

    @Override
    public LogisticsCompanyDO getLogiByCode(String code) {
        return logisticsCompanyManager.getLogiByCode(code);
    }

    @Override
    public LogisticsCompanyDO getModel(Long id) {
        return logisticsCompanyManager.getModel(id);
    }

    @Override
    public List<LogisticsCompanyDO> list() {
        return logisticsCompanyManager.list();
    }

    @Override
    public List<LogisticsCompanyDO> listAllNormal() {
        return logisticsCompanyManager.listAllNormal();
    }
}
