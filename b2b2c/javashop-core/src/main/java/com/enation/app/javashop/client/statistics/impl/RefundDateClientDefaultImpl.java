package com.enation.app.javashop.client.statistics.impl;

import com.enation.app.javashop.client.statistics.RefundDataClient;
import com.enation.app.javashop.model.statistics.dto.RefundData;
import com.enation.app.javashop.service.statistics.RefundDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * RefundDateClientDefaultImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-14 下午2:42
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class RefundDateClientDefaultImpl implements RefundDataClient {

    @Autowired
    private RefundDataManager refundDataManager;
    /**
     * 退款消息写入
     *
     * @param refundData
     */
    @Override
    public void put(RefundData refundData) {
        refundDataManager.put(refundData);
    }
}
