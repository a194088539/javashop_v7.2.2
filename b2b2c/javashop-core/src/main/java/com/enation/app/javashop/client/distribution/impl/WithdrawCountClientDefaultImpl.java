package com.enation.app.javashop.client.distribution.impl;

import com.enation.app.javashop.client.distribution.WithdrawCountClient;
import com.enation.app.javashop.service.distribution.WithdrawCountManager;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 可提现金额计算
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/5/23 上午7:46
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class WithdrawCountClientDefaultImpl implements WithdrawCountClient{


    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WithdrawCountManager withdrawCountManager;

    /**
     * 每天执行结算
     */
    @Override
    public void everyDay() {
        withdrawCountManager.withdrawCount();
    }
}
