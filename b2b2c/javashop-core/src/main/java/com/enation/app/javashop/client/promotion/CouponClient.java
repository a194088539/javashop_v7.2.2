package com.enation.app.javashop.client.promotion;

import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;

import java.util.List;

/**
 * @author zh
 * @version v2.0
 * @Description: 优惠券单对外接口
 * @date 2018/7/26 11:21
 * @since v7.0.0
 */
public interface CouponClient {
    /**
     * 修改优惠券的店铺名称
     *
     * @param shopId   店铺id
     * @param shopName 店铺名称
     */
    void editCouponShopName(Long shopId, String shopName);

    /**
     * 获取优惠券
     *
     * @param id 优惠券主键
     * @return Coupon  优惠券
     */
    CouponDO getModel(Long id);


    /**
     * 增加被领取数量
     *
     * @param couponId
     */
    void addReceivedNum(Long couponId);

    /**
     * 增加优惠券使用数量
     *
     * @param id
     */
    void addUsedNum(Long id);

    /**
     * 读取商家优惠券，正在进行中的
     *
     * @param sellerId
     * @return
     */
    List<CouponDO> getList(Long sellerId);


}
