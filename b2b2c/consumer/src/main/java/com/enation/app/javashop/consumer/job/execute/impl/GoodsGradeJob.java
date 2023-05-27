package com.enation.app.javashop.consumer.job.execute.impl;

import com.enation.app.javashop.consumer.job.execute.EveryDayExecute;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品评分定时任务
 *
 * @author fk
 * @version v1.0
 * @since v7.0
 * 2018-07-05 下午2:11
 */
@Component
public class GoodsGradeJob implements EveryDayExecute {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GoodsClient goodsClient;

    /**
     * 每晚23:30执行
     */
    @Override
    public void everyDay() {

        try{
            this.goodsClient.updateGoodsGrade();
        }catch (Exception e) {
            logger.error("计算商品评分出错", e);
        }

    }

}
