package com.enation.app.javashop.client.promotion.impl;

import com.enation.app.javashop.client.promotion.FullDiscountGiftClient;
import com.enation.app.javashop.model.goods.enums.QuantityType;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.service.promotion.fulldiscount.FullDiscountGiftManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 满送赠品client
 * @date 2020/4/3 11:21
 * @since v7.2.0
 */
@Service
public class FullDiscountGiftClientImpl implements FullDiscountGiftClient {

    @Autowired
    private FullDiscountGiftManager fullDiscountGiftManager;

    @Override
    public boolean addGiftQuantity(List<FullDiscountGiftDO> giftDOList) {
        return fullDiscountGiftManager.addGiftQuantity(giftDOList);
    }

    @Override
    public boolean reduceGiftQuantity(List<FullDiscountGiftDO> giftDOList, QuantityType type) {
        return fullDiscountGiftManager.reduceGiftQuantity(giftDOList, type);
    }

    @Override
    public boolean addGiftEnableQuantity(List<FullDiscountGiftDO> giftDOList) {
        return fullDiscountGiftManager.addGiftEnableQuantity(giftDOList);
    }

    @Override
    public FullDiscountGiftDO getModel(Long id) {
        return fullDiscountGiftManager.getModel(id);
    }
}
