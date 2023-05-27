package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.DepositeLogClient;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;
import com.enation.app.javashop.service.trade.deposite.DepositeLogManager;
import com.enation.app.javashop.framework.database.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @description: 预存款记录客户端实现
 * @author: liuyulei
 * @create: 2020-01-02 16:32
 * @version:1.0
 * @since:7.1.4
 **/
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class DepositeLogClientImpl implements DepositeLogClient {


    @Autowired
    private DepositeLogManager depositeLogManager;

    @Override
    public void add(DepositeLogDO logDO) {
        depositeLogManager.add(logDO);
    }

    @Override
    public WebPage list(DepositeParamDTO paramDTO) {
        return this.depositeLogManager.list(paramDTO);
    }
}
