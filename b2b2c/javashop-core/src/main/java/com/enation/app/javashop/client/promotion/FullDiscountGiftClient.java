package com.enation.app.javashop.client.promotion;


import com.enation.app.javashop.model.goods.enums.QuantityType;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;

import java.util.List;

/**
 * @author fk
 * @version v2.0
 * @Description: 满送赠品client
 * @date 2020/4/3 11:21
 * @since v7.2.0
 */
public interface FullDiscountGiftClient {


    /**
     * 增加赠品库存
     * @param giftDOList
     * @return
     */
    boolean addGiftQuantity(List<FullDiscountGiftDO> giftDOList);

    /**
     * 减少赠品库存
     * @param giftDOList
     * @param type
     * @return
     */
    boolean reduceGiftQuantity(List<FullDiscountGiftDO> giftDOList, QuantityType type);

    /**
     * 增加赠品可用库存
     * @param giftDOList
     * @return
     */
    boolean addGiftEnableQuantity(List<FullDiscountGiftDO> giftDOList);

    /**
     * 获取满优惠赠品
     * @param id 满优惠赠品主键
     * @return FullDiscountGift  满优惠赠品
     */
    FullDiscountGiftDO getModel(Long id);

}
