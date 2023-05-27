package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;

import com.enation.app.javashop.model.trade.cart.enums.CartType;
import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.cart.vo.*;
import com.enation.app.javashop.service.trade.cart.cartbuilder.*;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 促销信息构建器
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/10
 */
public class DefaultCartBuilder implements CartBuilder {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 购物车促销规则渲染器
     */
    private CartPromotionRenderer cartPromotionRenderer;

    /**
     * 购物车价格计算器
     */
    private CartPriceCalculator cartPriceCalculator;

    /**
     * 数据校验
     */
    private CheckDataRebderer checkDataRebderer;

    /**
     * 购物车sku渲染器
     */
    private CartSkuRenderer cartSkuRenderer;

    /**
     * 购物车优惠券渲染
     */
    private CartCouponRenderer cartCouponRenderer;


    /**
     * 运费价格计算器
     */
    private CartShipPriceCalculator cartShipPriceCalculator;


    private List<CartVO> cartList;
    private PriceDetailVO price;
    private CartType cartType;
    private List<CouponVO> couponList;


    public DefaultCartBuilder(CartType cartType, CartSkuRenderer cartSkuRenderer, CartPromotionRenderer cartPromotionRenderer, CartPriceCalculator cartPriceCalculator, CheckDataRebderer checkDataRebderer) {
        this.cartType = cartType;
        this.cartSkuRenderer = cartSkuRenderer;
        this.cartPromotionRenderer = cartPromotionRenderer;
        this.cartPriceCalculator = cartPriceCalculator;
        this.checkDataRebderer = checkDataRebderer;
        cartList = new ArrayList<>();
    }

    public DefaultCartBuilder(CartType cartType, CartSkuRenderer cartSkuRenderer, CartPromotionRenderer cartPromotionRenderer, CartPriceCalculator cartPriceCalculator, CartCouponRenderer cartCouponRenderer, CartShipPriceCalculator cartShipPriceCalculator, CheckDataRebderer checkDataRebderer) {
        this.cartType = cartType;
        this.cartSkuRenderer = cartSkuRenderer;
        this.cartPromotionRenderer = cartPromotionRenderer;
        this.cartPriceCalculator = cartPriceCalculator;
        this.cartCouponRenderer = cartCouponRenderer;
        this.cartShipPriceCalculator = cartShipPriceCalculator;
        this.checkDataRebderer = checkDataRebderer;
        cartList = new ArrayList<>();
    }

    /**
     * 渲染sku<br/>
     * 此步通过{@link com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo}生产出一个全新的cartList
     *
     * @return
     */
    @Override
    public CartBuilder renderSku(CheckedWay way) {
        cartSkuRenderer.renderSku(this.cartList, cartType, way);
        return this;
    }

    /**
     * 带过滤器式的渲染sku<br/>
     * 可以过滤为指定条件{@link  CartSkuFilter}的商品<br/>
     *
     * @return
     * @see CartSkuFilter
     */
    @Override
    public CartBuilder renderSku(CartSkuFilter filter, CheckedWay way) {
        cartSkuRenderer.renderSku(this.cartList, filter, cartType, way);
        return this;
    }


    /**
     * 此步通过
     * {@link com.enation.app.javashop.model.trade.cart.vo.SelectedPromotionVo}
     * @return
     */
    @Override
    public CartBuilder renderPromotion() {
        cartPromotionRenderer.render(cartList);
        return this;
    }

    /**
     * 此步通过上一步的产出物
     * {@link com.enation.app.javashop.model.trade.cart.vo.PromotionRule}
     * 来计算出价格:
     * {@link PriceDetailVO}
     *
     * @return
     */
    @Override
    public CartBuilder countPrice(Boolean includeCoupon) {

        this.price = cartPriceCalculator.countPrice(cartList,includeCoupon);
        return this;
    }


    /**
     * 调用运费模板来算出运费，只接应用到购物车的价格中
     *
     * @return
     */
    @Override
    public CartBuilder countShipPrice() {
        cartShipPriceCalculator.countShipPrice(cartList);
        return this;
    }

    /**
     * 此步读取出会员的可用优惠券，加入到购物车的couponList中
     *
     * @return
     */
    @Override
    public CartBuilder renderCoupon() {
        List<CouponVO> couponList = cartCouponRenderer.render(cartList);
        this.couponList = couponList;
        return this;
    }

    @Override
    public CartView build() {
        return new CartView(cartList, price, couponList);
    }

    @Override
    public CartBuilder checkData() {
        checkDataRebderer.checkData(cartList);
        return this;
    }
}
