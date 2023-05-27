package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;

import java.util.List;

/**
 * 订单元信息
 * @author Snow create in 2018/6/27
 * @version v2.0
 * @since v7.0.0
 */
public interface OrderMetaManager {

    /**
     * 添加
     * @param orderMetaDO
     */
    void add(OrderMetaDO orderMetaDO);

    /**
     * 读取订单元信息
     * @param orderSn
     * @param metaKey
     * @return
     */
    String getMetaValue(String orderSn,OrderMetaKeyEnum metaKey);

    /**
     * 读取order meta列表
     * @param orderSn
     * @return
     */
    List<OrderMetaDO> list(String orderSn);

    /**
     * 修改订单元信息
     * @param orderSn
     * @param metaKey
     * @param metaValue
     * @return
     */
    void updateMetaValue(String orderSn,OrderMetaKeyEnum metaKey, String metaValue);

    /**
     * 修改订单元信息状态
     * @param orderSn
     * @param metaKey
     * @param status
     * @return
     */
    void updateMetaStatus(String orderSn, OrderMetaKeyEnum metaKey, String status);

    /**
     * 获取一条订单元信息
     * @param orderSn 订单编号
     * @param metaKey 扩展-键
     * @return
     */
    OrderMetaDO getModel(String orderSn, OrderMetaKeyEnum metaKey);

    /**
     * 获取订单赠品信息集合
     * @param orderSn 订单编号
     * @param status 订单赠品的售后状态
     * @return
     */
    List<FullDiscountGiftDO> getGiftList(String orderSn, String status);
}
