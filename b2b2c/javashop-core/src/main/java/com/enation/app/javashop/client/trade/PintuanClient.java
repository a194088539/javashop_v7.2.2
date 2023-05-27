package com.enation.app.javashop.client.trade;


import com.enation.app.javashop.model.promotion.pintuan.Pintuan;

import java.util.List;

/**
 * 拼团client
 *
 * @author liushuai
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2019/2/18 上午11:39
 */

public interface PintuanClient {


    /**
     * 获取拼团
     *
     * @param id 拼团主键
     * @return Pintuan  拼团
     */
    Pintuan getModel(Long id);

    /**
     * 根据状态查询拼团活动
     *
     * @param status 状态
     * @return 拼团活动集合
     */
    List<Pintuan> get(String status);

    /**
     * 停止一个活动
     *
     * @param promotionId
     */
    void closePromotion(Long promotionId);

    /**
     * 开始一个活动
     *
     * @param promotionId
     */
    void openPromotion(Long promotionId);
}
