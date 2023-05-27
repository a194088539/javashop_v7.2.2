package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.statistics.vo.SimpleChart;


/**
 * 分销商统计
 *
 * @author Chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/13 上午8:36
 */
public interface DistributionStatisticManager {


    /**
     * 营业额
     *
     * @param circle
     * @param memberId
     * @param year
     * @param month
     * @return
     */
    SimpleChart getOrderMoney(String circle, Long memberId, Integer year, Integer month);

    /**
     * 利润
     *
     * @param circle
     * @param memberId
     * @param year
     * @param month
     * @return
     */
    SimpleChart getPushMoney(String circle, Long memberId, Integer year, Integer month);

    /**
     * 订单数
     *
     * @param circle
     * @param memberId
     * @param year
     * @param month
     * @return
     */
    SimpleChart getOrderCount(String circle, Long memberId, Integer year, Integer month);


    /**
     * 店铺统计
     * @param circle
     * @param year
     * @param month
     * @param pageSize
     * @param pageNo
     * @return
     */
    WebPage getShopPush(String circle, Integer year, Integer month, Long pageSize, Long pageNo);

}
