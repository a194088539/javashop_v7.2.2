package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.RechargeClient;
import com.enation.app.javashop.model.member.dto.DepositeParamDTO;
import com.enation.app.javashop.service.trade.deposite.RechargeManager;
import com.enation.app.javashop.framework.database.WebPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @description: 预存款充值客户端实现
 * @author: liuyulei
 * @create: 2020-01-02 16:43
 * @version:1.0
 * @since:7.1.4
 **/
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class RechargeClientImpl implements RechargeClient {

    @Autowired
    private RechargeManager rechargeManager;

    @Override
    public WebPage list(DepositeParamDTO paramDTO) {
        return rechargeManager.list(paramDTO);
    }

}
