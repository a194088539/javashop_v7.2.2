package com.enation.app.javashop.client.promotion.impl;

import com.enation.app.javashop.client.promotion.CouponClient;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.service.promotion.coupon.CouponManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 优惠券client实现
 *
 * @author zh
 * @version v7.0
 * @date 18/12/6 下午4:28
 * @since v7.0
 */
@Service
public class CouponClientImpl implements CouponClient {


    @Autowired
    private CouponManager couponManager;

    @Override
    public void editCouponShopName(Long shopId, String shopName) {
        couponManager.editCouponShopName(shopId, shopName);
    }

    @Override
    public CouponDO getModel(Long id) {
        return couponManager.getModel(id);
    }

    @Override
    public void addReceivedNum(Long couponId) {
        couponManager.addReceivedNum(couponId);
    }

    @Override
    public void addUsedNum(Long id) {

        couponManager.addUsedNum(id);
    }

    @Override
    public List<CouponDO> getList(Long sellerId) {
        return couponManager.getList(sellerId);
    }


}
