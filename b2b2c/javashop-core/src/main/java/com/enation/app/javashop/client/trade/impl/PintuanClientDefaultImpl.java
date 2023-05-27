package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.PintuanClient;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.service.trade.pintuan.PintuanManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PintuanClientDefaultImpl
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2019-02-18 上午11:44
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class PintuanClientDefaultImpl implements PintuanClient {
    @Autowired
    private PintuanManager pintuanManager;

    /**
     * 获取拼团
     *
     * @param id 拼团主键
     * @return Pintuan  拼团
     */
    @Override
    public Pintuan getModel(Long id) {
        return pintuanManager.getModel(id);
    }


    @Override
    public List<Pintuan> get(String status) {
        return pintuanManager.get(status);
    }

    @Override
    public void closePromotion(Long promotionId) {
        pintuanManager.closePromotion(promotionId);
    }

    @Override
    public void openPromotion(Long promotionId) {
        pintuanManager.openPromotion(promotionId);
    }
}
