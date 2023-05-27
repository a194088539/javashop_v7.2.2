package com.enation.app.javashop.model.promotion.coupon.vo;

import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.framework.util.CurrencyUtil;

import java.io.Serializable;

/**
 * 商品优惠券金额
 *
 * @author fk
 * @version v2.0
 * @since v7.1.5
 * 2019-09-19 23:19:39
 */
public class GoodsCouponPrice implements Serializable {

    /**
     * skuid
     */
    private Long skuId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 优惠券抵扣的金额
     */
    private Double couponPrice;

    /**
     * 商品金额
     */
    private Double goodsOriginPrice;

    /**
     * 商家id
     */
    private Long sellerId;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 会员优惠券id
     */
    private Long memberCouponId;


    public GoodsCouponPrice() {

    }

    public GoodsCouponPrice(CartSkuVO skuVO) {

        this.setGoodsId(skuVO.getGoodsId());
        this.setSkuId(skuVO.getSkuId());
        this.setGoodsOriginPrice(CurrencyUtil.mul(skuVO.getOriginalPrice(),skuVO.getNum()));
        this.setSellerId(skuVO.getSellerId());
    }


    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Double getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(Double couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Double getGoodsOriginPrice() {
        return goodsOriginPrice;
    }

    public void setGoodsOriginPrice(Double goodsOriginPrice) {
        this.goodsOriginPrice = goodsOriginPrice;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getMemberCouponId() {
        return memberCouponId;
    }

    public void setMemberCouponId(Long memberCouponId) {
        this.memberCouponId = memberCouponId;
    }
}
