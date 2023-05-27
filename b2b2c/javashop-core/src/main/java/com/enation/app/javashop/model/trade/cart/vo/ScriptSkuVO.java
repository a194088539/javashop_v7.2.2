package com.enation.app.javashop.model.trade.cart.vo;

import com.enation.app.javashop.framework.util.CurrencyUtil;

import java.io.Serializable;

/**
 * @description: script脚本变量对象
 * @author: liuyulei
 * @create: 2020-01-09 09:26
 * @version:1.0
 * @since:7.1.4
 **/
public class ScriptSkuVO implements Serializable {
    private static final long serialVersionUID = -6297338865669131324L;

    /**
     * skuid
     */
    private Long $skuId;
    /**
     * 购买数量
     */
    private Integer $num;

    /**
     * 商品价格-原价
     */
    private Double $price;

    /**
     * 商品小计 -按照原价计算
     */
    private Double $totalPrice;


    public ScriptSkuVO(CartSkuVO  cartSkuVO) {
        this.$skuId = cartSkuVO.getSkuId();
        this.$num = cartSkuVO.getNum();
        this.$price = cartSkuVO.getOriginalPrice();
        this.$totalPrice = CurrencyUtil.mul(this.$price,this.$num);
    }

    public ScriptSkuVO() {
    }

    public Long get$skuId() {
        return $skuId;
    }

    public void set$skuId(Long $skuId) {
        this.$skuId = $skuId;
    }

    public Integer get$num() {
        return $num;
    }

    public void set$num(Integer $num) {
        this.$num = $num;
    }

    public Double get$price() {
        return $price;
    }

    public void set$price(Double $price) {
        this.$price = $price;
    }

    public Double get$totalPrice() {
        return $totalPrice;
    }

    public void set$totalPrice(Double $totalPrice) {
        this.$totalPrice = $totalPrice;
    }

    @Override
    public String toString() {
        return "ScriptSkuVO{" +
                "$skuId=" + $skuId +
                ", $num=" + $num +
                ", $price=" + $price +
                ", $totalPrice=" + $totalPrice +
                '}';
    }
}
