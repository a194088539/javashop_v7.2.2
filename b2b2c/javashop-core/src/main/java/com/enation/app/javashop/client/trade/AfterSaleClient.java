package com.enation.app.javashop.client.trade;

import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;

/**
 * 售后client
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/2
 */
public interface AfterSaleClient {

    /**
     * 取消拼团订单
     * @param orderSn 订单编号
     * @param cancelReason 取消原因
     */
    void cancelPintuanOrder(String orderSn, String cancelReason);

    /**
     * 获取售后服务详细
     * @param serviceSn 售后服务单号
     * @return
     */
    ApplyAfterSaleVO detail(String serviceSn);


    /**
     * 获取退款单 调用afterSaleRefundManager.getModel
     * @param serviceSn 售后服务单号
     * @return
     */
    RefundDO getAfterSaleRefundModel(String serviceSn);


    /**
     * 退款完成
     */
    void refundCompletion();

    /**
     * 修改售后服务单中的店铺名称
     * 当商家修改店铺名称是调用此方法
     *
     * @param shopId   店铺id
     * @param shopName 店铺名称
     */
    void editAfterSaleShopName(Long shopId, String shopName);

}
