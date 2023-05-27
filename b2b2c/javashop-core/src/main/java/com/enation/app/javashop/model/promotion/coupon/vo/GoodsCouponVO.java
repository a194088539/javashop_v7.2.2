package com.enation.app.javashop.model.promotion.coupon.vo;


import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 优惠券VO
 * @author: fk
 * @create: 2020-04-15 09:59
 * @version:1.0
 * @since:7.1.4
 **/
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GoodsCouponVO implements Serializable {

    /**
     * skuid
     */
    private Long skuId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 该sku可用的优惠券列表
     */
    private List<CouponDO> couponList;


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

    public List<CouponDO> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<CouponDO> couponList) {
        this.couponList = couponList;
    }
}
