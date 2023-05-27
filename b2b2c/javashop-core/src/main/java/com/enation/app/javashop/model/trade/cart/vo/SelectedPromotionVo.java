package com.enation.app.javashop.model.trade.cart.vo;

import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * Created by kingapex on 2018/12/9.
 * 用户选择的优惠活动和优惠券<br/>
 * 以每个店铺的sellerId为key形成map
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/9
 */
public class SelectedPromotionVo implements Serializable {


    private static final long serialVersionUID = -5721652966259085432L;
    /**
     * 用户选择的优惠券
     * 以sellerId为key，以CouponVo为value
     */
    private Map<Long, CouponVO> couponMap;

    /**
     * 用户选择的单品优惠活动
     * 以sellerId为key，value为List<CartPromotionVo>
     */
    private Map<Long, List<CartPromotionVo>> singlePromotionMap;

    /**
     * 用户选择的组合活动
     * 以sellerId为key，value为PromotionVO
     */
    private Map<Long, CartPromotionVo> groupPromotionMap;


    /**
     * 构造器，初始化map
     */
    public SelectedPromotionVo() {
        this.couponMap = new HashMap<Long, CouponVO>(16);
        this.singlePromotionMap = new HashMap<Long, List<CartPromotionVo>>(16);
        this.groupPromotionMap = new HashMap<Long, CartPromotionVo>(16);
    }


    /**
     * 压入一个优惠券
     * @param sellerId 店铺id
     * @param coupon 优惠券
     */
    public void putCooupon(Long sellerId, CouponVO coupon){
        if (coupon == null) {
            return;
        }

        couponMap.put(sellerId, coupon);
    }


    /**
     * 向map中压入一个商品的促销活动
     * @param sellerId 对应的店铺id
     * @param usedPromotionVo 某sku的要使用的促销活动
     */
    public void putPromotion(Long sellerId, CartPromotionVo usedPromotionVo){
        if (usedPromotionVo == null) {
            return;
        }

        //只有满减是组合活动，其它全是单品活动
        if (usedPromotionVo.getPromotionType().equals(PromotionTypeEnum.FULL_DISCOUNT.name())) {
            //如果是组合活动则将skuid重置为空
            usedPromotionVo.setSkuId(null);
            groupPromotionMap.put(sellerId, usedPromotionVo);
        }else{
            this.putSinglePromotion(sellerId,usedPromotionVo);
        }

    }


    /**
     * 设置单品促销活动
     * @param sellerId 店铺id
     * @param usedPromotionVo  要使用的单品活动
     */
    private  void putSinglePromotion(Long sellerId, CartPromotionVo usedPromotionVo){

        if (singlePromotionMap == null) {
            singlePromotionMap = new HashMap<Long, List<CartPromotionVo>>();
        }

        //先试着读取此店铺的每个sku的促销列表，如果为空，则新创建一个
        List<CartPromotionVo> skuPromotionList = singlePromotionMap.get(sellerId);
        if (skuPromotionList == null) {
            skuPromotionList = new ArrayList();
            singlePromotionMap.put(sellerId,skuPromotionList);
        }


        //查找当前sku是否有促销活动，如果有，先删掉（单品活动不能重叠）
        Iterator<CartPromotionVo> iterator = skuPromotionList.iterator();
        while (iterator.hasNext()) {
            CartPromotionVo promotionVO = iterator.next();
            if (promotionVO.getSkuId().equals(usedPromotionVo.getSkuId())) {
                iterator.remove();
            }
        }

        //传引用，不用重新压回了
        skuPromotionList.add(usedPromotionVo);
    }


    @Override
    public String toString() {
        return "SelectedPromotionVo{" +
                "couponMap=" + couponMap +
                ", singlePromotionMap=" + singlePromotionMap +
                ", groupPromotionMap=" + groupPromotionMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SelectedPromotionVo that = (SelectedPromotionVo) o;

        return new EqualsBuilder()
                .append(couponMap, that.couponMap)
                .append(singlePromotionMap, that.singlePromotionMap)
                .append(groupPromotionMap, that.groupPromotionMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(couponMap)
                .append(singlePromotionMap)
                .append(groupPromotionMap)
                .toHashCode();
    }

    public Map<Long, CartPromotionVo> getGroupPromotionMap() {
        return groupPromotionMap;
    }

    public void setGroupPromotionMap(Map<Long, CartPromotionVo> groupPromotionMap) {
        this.groupPromotionMap = groupPromotionMap;
    }

    public Map<Long, CouponVO> getCouponMap() {
        return couponMap;
    }

    public void setCouponMap(Map<Long, CouponVO> couponMap) {
        this.couponMap = couponMap;
    }

    public Map<Long, List<CartPromotionVo>> getSinglePromotionMap() {
        if (singlePromotionMap == null) {
            return new HashMap<>(16);
        }
        return singlePromotionMap;
    }

    public void setSinglePromotionMap(Map<Long, List<CartPromotionVo>> singlePromotionMap) {
        this.singlePromotionMap = singlePromotionMap;
    }
}
