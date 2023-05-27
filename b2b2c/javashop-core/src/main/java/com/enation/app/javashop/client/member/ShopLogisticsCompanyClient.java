package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.shop.dos.ShopLogisticsSetting;

import java.util.List;

/**
 * 店铺物流client
 *
 * @author fk
 * @version v7.0
 * @date 19/7/27 下午3:51
 * @since v7.0
 */
public interface ShopLogisticsCompanyClient {

    /**
     * 查询绑定该物流公司的列表
     * @param logisticsId
     * @return
     */
    List queryListByLogisticsId(Long logisticsId);

    /**
     * 删除绑定某物流公司的所有店铺
     * @param logisticsId
     */
    void deleteByLogisticsId(Long logisticsId);

    /**
     * 查询物流公司
     * @param logisticsId
     * @param sellerId
     * @return
     */
    ShopLogisticsSetting query(Long logisticsId, Long sellerId);

}
