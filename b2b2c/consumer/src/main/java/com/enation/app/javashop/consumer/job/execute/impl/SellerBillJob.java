package com.enation.app.javashop.consumer.job.execute.impl;

import com.enation.app.javashop.consumer.job.execute.EveryMonthExecute;
import com.enation.app.javashop.client.trade.BillClient;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商家结算单生成 状态修改
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-19 下午2:49
 */
@Component
public class SellerBillJob implements EveryMonthExecute {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private BillClient billClient;

    /**
     * 每月执行
     */
    @Override
    public void everyMonth() {
        try {
            this.logger.debug("-----生成结算单执行-----");
            // 上个月的开始结束时间
            Long[] time = DateUtil.getLastMonth();
            billClient.createBills(time[0],time[1]);
        } catch (Exception e) {
            this.logger.error("每月生成结算单异常：", e);
        }
    }
}
