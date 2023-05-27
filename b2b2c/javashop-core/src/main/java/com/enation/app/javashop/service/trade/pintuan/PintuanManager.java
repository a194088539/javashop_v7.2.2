package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.model.promotion.pintuan.PintuanQueryParam;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;

import java.util.List;

/**
 * 拼团业务层
 *
 * @author admin
 * @version vv1.0.0
 * @since vv7.1.0
 * 2019-01-21 15:17:57
 */
public interface PintuanManager {

    /**
     * 查询拼团列表
     *
     * @param param 搜索参数
     * @return WebPage
     */
    WebPage list(PintuanQueryParam param);

    /**
     * 根据当前状态查询活动
     *
     * @param status 状态
     * @return 拼团活动集合
     */
    List<Pintuan> get(String status);

    /**
     * 添加拼团
     *
     * @param pintuan 拼团
     * @return Pintuan 拼团
     */
    Pintuan add(Pintuan pintuan);

    /**
     * 修改拼团
     *
     * @param pintuan 拼团
     * @param id      拼团主键
     * @return Pintuan 拼团
     */
    Pintuan edit(Pintuan pintuan, Long id);

    /**
     * 删除拼团
     *
     * @param id 拼团主键
     */
    void delete(Long id);

    /**
     * 获取拼团
     *
     * @param id 拼团主键
     * @return Pintuan  拼团
     */
    Pintuan getModel(Long id);


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

    /**
     * 停止一个活动
     *
     * @param promotionId
     */
    void manualClosePromotion(Long promotionId);

    /**
     * 开始一个活动
     *
     * @param promotionId
     */
    void manualOpenPromotion(Long promotionId);

}
