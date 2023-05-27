package com.enation.app.javashop.consumer.job.execute.impl;

import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.consumer.job.execute.EveryHourExecute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定时任务查询退款状态并且修改退款状态
 *
 * @author zh
 * @version v1.0
 * @since v7.1.5
 * 2020-02-03
 */
@Component
public class RefundStatusJobConsumer implements EveryHourExecute {

    @Autowired
    private AfterSaleClient afterSaleClient;

    @Override
    public void everyHour() {
        afterSaleClient.refundCompletion();
    }
}
