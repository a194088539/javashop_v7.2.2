package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberCouponClient;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.service.member.MemberCouponManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员优惠券默认实现
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 下午3:54
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class MemberCouponDefaultImpl implements MemberCouponClient {

    @Autowired
    private MemberCouponManager memberCouponManager;

    @Override
    public List<MemberCoupon> listByCheckout(Long[] sellerIds, Long memberId) {
        return memberCouponManager.listByCheckout(sellerIds, memberId);
    }

    @Override
    public MemberCoupon getModel(Long memberId, Long mcId) {
        return memberCouponManager.getModel(memberId, mcId);
    }

    @Override
    public void updateSellerName(Long sellerId, String sellerName) {
        memberCouponManager.updateSellerName(sellerId,sellerName);
    }

}
