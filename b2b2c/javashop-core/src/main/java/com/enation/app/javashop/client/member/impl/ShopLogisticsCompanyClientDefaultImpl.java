package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.ShopLogisticsCompanyClient;
import com.enation.app.javashop.model.shop.dos.ShopLogisticsSetting;
import com.enation.app.javashop.service.shop.ShopLogisticsCompanyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 店铺物流client实现
 *
 * @author fk
 * @version v7.0
 * @date 19/7/27 下午3:51
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class ShopLogisticsCompanyClientDefaultImpl implements ShopLogisticsCompanyClient {


    @Autowired
    private ShopLogisticsCompanyManager shopLogisticsCompanyManager;

    @Override
    public List queryListByLogisticsId(Long logisticsId) {

        return shopLogisticsCompanyManager.queryListByLogisticsId(logisticsId);
    }

    @Override
    public void deleteByLogisticsId(Long logisticsId) {

        shopLogisticsCompanyManager.deleteByLogisticsId(logisticsId);
    }

    @Override
    public ShopLogisticsSetting query(Long logisticsId, Long sellerId) {
        return shopLogisticsCompanyManager.query(logisticsId,sellerId);
    }
}
