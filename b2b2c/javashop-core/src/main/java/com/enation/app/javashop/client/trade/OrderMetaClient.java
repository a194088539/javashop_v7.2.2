package com.enation.app.javashop.client.trade;


import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;

import java.util.List;

/**
 * 订单操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
public interface OrderMetaClient {


    /**
     * 添加OrderMeta
     * @param orderMetaDO
     */
    void add(OrderMetaDO orderMetaDO);

    /**
     * 获取订单赠品信息集合
     * @param orderSn 订单编号
     * @param status 订单赠品的售后状态
     * @return
     */
    List<FullDiscountGiftDO> getGiftList(String orderSn, String status);

    /**
     * 修改订单元信息状态
     * @param orderSn
     * @param metaKey
     * @param status
     * @return
     */
    void updateMetaStatus(String orderSn, OrderMetaKeyEnum metaKey, String status);

    /**
     * 读取订单元信息
     * @param orderSn
     * @param metaKey
     * @return
     */
    String getMetaValue(String orderSn,OrderMetaKeyEnum metaKey);

}
