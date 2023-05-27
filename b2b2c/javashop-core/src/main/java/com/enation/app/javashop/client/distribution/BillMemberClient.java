package com.enation.app.javashop.client.distribution;

import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import com.enation.app.javashop.model.distribution.dto.DistributionRefundDTO;

/**
 * DistributionClient
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-14 下午1:54
 */
public interface BillMemberClient {

    /**
     * 购买商品产生的结算
     *
     * @param order
     */
    void buyShop(DistributionOrderDO order);


    /**
     * 退货商品产生的结算
     *
     * @param order
     */
    void returnShop(DistributionOrderDO order, DistributionRefundDTO distributionRefundDTO);
}
