package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;

import java.lang.Double;
import java.util.ArrayList;

import com.enation.app.javashop.model.goods.enums.GoodsType;
import com.enation.app.javashop.model.promotion.tool.enums.PromotionTypeEnum;
import com.enation.app.javashop.model.promotion.tool.vo.GiveGiftVO;
import com.enation.app.javashop.model.trade.cart.enums.CartType;
import com.enation.app.javashop.model.trade.cart.vo.*;
import com.enation.app.javashop.service.trade.cart.CartPromotionManager;
import com.enation.app.javashop.service.trade.cart.cartbuilder.CartPriceCalculator;
import com.enation.app.javashop.service.trade.cart.cartbuilder.ScriptProcess;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by kingapex on 2018/12/10.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/10
 */
@Service(value = "cartPriceCalculator")
public class CartPriceCalculatorImpl implements CartPriceCalculator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CartPromotionManager cartPromotionManager;

    @Autowired
    private ScriptProcess scriptProcess;


    @Override
    public PriceDetailVO countPrice(List<CartVO> cartList, Boolean includeCoupon) {

        //如果不包含优惠券计算，则将选中的优惠券信息删除
        if (!includeCoupon) {
            cartPromotionManager.cleanCoupon();
        }

        //根据规则计算价格
        PriceDetailVO priceDetailVO = this.countPriceWithScript(cartList, includeCoupon);

        return priceDetailVO;
    }


    /**
     * 根据促销脚本计算价格
     *
     * @param cartList      购物车列表
     * @param includeCoupon 是否包含优惠券
     * @return
     */
    private PriceDetailVO countPriceWithScript(List<CartVO> cartList, boolean includeCoupon) {

        PriceDetailVO price = new PriceDetailVO();
        //获取选中的促销活动
        SelectedPromotionVo selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        //用户选择的组合活动
        Map<Long, CartPromotionVo> groupPromotionMap = selectedPromotionVo.getGroupPromotionMap();
        //用户选择的单品活动
        Map<Long, List<CartPromotionVo>> singlePromotionMap = selectedPromotionVo.getSinglePromotionMap();
        //用户选择使用的优惠券
        Map<Long, CouponVO> couponMap = selectedPromotionVo.getCouponMap();

        for (CartVO cart : cartList) {
            //未选中的购物车不参与计算
            if (cart.getChecked() == 0) {
                continue;
            }
            //获取当前购物车用户选择的组合活动
            CartPromotionVo groupPromotion = groupPromotionMap.get(cart.getSellerId());
            //获取当前购物车用户选择的单品活动
            List<CartPromotionVo> singlePromotions = singlePromotionMap.get(cart.getSellerId());

            PriceDetailVO cartPrice = new PriceDetailVO();
            cartPrice.setFreightPrice(cart.getPrice().getFreightPrice());
            List<CartSkuVO> groupSkuList = new ArrayList<>();
            for (CartSkuVO cartSku : cart.getSkuList()) {
                //未选中的商品不参与计算
                if (cartSku.getChecked() == 0) {
                    continue;
                }
                //计算单品活动促销优惠
                this.calculatorSingleScript(cart, singlePromotions, cartPrice, cartSku);
                if (cartSku.getGroupList().contains(groupPromotion)) {
                    groupSkuList.add(cartSku);
                }
            }
            //计算满减优惠，返回是否免运费
            Boolean freeShipping = calculatorGroupScript(cart, groupPromotion, cartPrice, groupSkuList);

            //单品规则中有免运费或满减里有免运费
            if (freeShipping) {
                cartPrice.setIsFreeFreight(1);
                cartPrice.setFreightPrice(0D);
            }

            //是否计算优惠券优惠价格
            Long sellerId = cart.getSellerId();
            if (includeCoupon) {
                CouponVO couponVO = couponMap.get(sellerId);
                calculatorCoupon(cartPrice, couponVO);

            }

            //计算店铺商品总优惠金额
            double totalDiscount = CurrencyUtil.add(cartPrice.getCashBack(), cartPrice.getCouponPrice());
            cartPrice.setDiscountPrice(totalDiscount);

            //总价为商品价加运费
            double totalPrice = CurrencyUtil.add(cartPrice.getTotalPrice(), cartPrice.getFreightPrice());
            cartPrice.setTotalPrice(totalPrice);

            cart.setPrice(cartPrice);
            price = price.plus(cartPrice);

        }
        //是否计算优惠券价格 此处针对平台优惠券
        if (includeCoupon) {
            CouponVO couponVO = couponMap.get(0L);
            calculatorCoupon(price, couponVO);
        }

        logger.debug("计算完优惠后购物车数据为：");
        logger.debug(cartList);

        logger.debug("价格为：");
        logger.debug(price);

        return price;
    }

    /**
     * 计算组合活动优惠
     *
     * @param cart           购物车
     * @param groupPromotion 组合活动
     * @param cartPrice      购物车价格VO
     * @param groupSkuList   参与组合活动的skuVO
     * @return 是否免运费  true 免运费   false不免运费
     */
    private Boolean calculatorGroupScript(CartVO cart, CartPromotionVo groupPromotion, PriceDetailVO cartPrice, List<CartSkuVO> groupSkuList) {
        Boolean freeShipping = false;
        if (groupPromotion != null && groupSkuList.size() > 0) {
            Double cost;
            String giftJson;
            Double totalGroupPrice = 0D;
            for (CartSkuVO cartSku : groupSkuList) {
                //如果商品是积分商品，则不参与满减优惠
//                if (GoodsType.POINT.name().equals(cartSku.getGoodsType())) {
//                    continue;
//                }
                totalGroupPrice += cartSku.getSubtotal();
            }
            Map param = new HashedMap();
            param.put("$price", totalGroupPrice);
            cost = scriptProcess.countPrice(groupPromotion.getPromotionScript(), param);
            //获取赠品信息
            giftJson = scriptProcess.giveGift(groupPromotion.getPromotionScript(), param);
            //设置购物车赠品信息
            cart.setGiftJson(giftJson);
            //计算优惠前后差价
            Double diffPrice = CurrencyUtil.sub(totalGroupPrice, cost);
            //设置返现金额
            cartPrice.setCashBack(CurrencyUtil.add(cartPrice.getCashBack(), diffPrice));
            //设置满减优惠金额
            cartPrice.setFullMinus(diffPrice);
            //设置优惠后的金额
            cartPrice.setTotalPrice(CurrencyUtil.sub(cartPrice.getTotalPrice(), diffPrice));
            if (!StringUtil.isEmpty(giftJson)) {
                List<GiveGiftVO> giftList = JsonUtil.jsonToList(giftJson, GiveGiftVO.class);
                for (GiveGiftVO giveGiftVO : giftList) {
                    if ("freeShip".equals(giveGiftVO.getType())) {
                        freeShipping = (Boolean) giveGiftVO.getValue();
                    }
                }
            }
        }
        return freeShipping;
    }

    /**
     * 计算单品活动优惠
     *
     * @param cart             购物车
     * @param singlePromotions 参与的单品活动
     * @param cartPrice        当前购物车价格
     * @param cartSku          当前skuVO
     */
    private void calculatorSingleScript(CartVO cart, List<CartPromotionVo> singlePromotions, PriceDetailVO cartPrice, CartSkuVO cartSku) {
        Double cost;
        Integer point = 0;
        if (CartType.CHECKOUT.equals(cart.getCartType()) && cartSku.getChecked() == 0) {
            return;
        }

        CartPromotionVo promotionVo = null;
        if (singlePromotions != null) {
            for (CartPromotionVo cartPromotionVo : singlePromotions) {
                if (cartSku.getSkuId().equals(cartPromotionVo.getSkuId())) {
                    promotionVo = cartPromotionVo;
                    break;
                }
            }
        }
        //转换脚本参数
        ScriptSkuVO skuVO = new ScriptSkuVO(cartSku);
        Map param = new HashedMap();
        param.put("$sku", skuVO);
        //商品参与了用户选择的活动
        if (promotionVo != null) {
            //获取优惠后的价格
            cost = scriptProcess.countPrice(promotionVo.getPromotionScript(), param);
            //获取积分兑换所需积分
            logger.debug("用户选择参与的促销活动类型:" + promotionVo.getPromotionType());
            if (PromotionTypeEnum.EXCHANGE.name().equals(promotionVo.getPromotionType())) {
                point = scriptProcess.countPoint(promotionVo.getPromotionScript(), param);
            }
            //设置商品单品成交单价
            calculatorPurchasePrice(cartSku, cost, promotionVo.getPromotionType());

            cartSku.setPoint(point);
            cartSku.setSubtotal(cost);
        }

        //未选中的不计入合计中
        if (cartSku.getChecked() == 0) {
            return;
        }

        //购物车全部商品的原价合
        cartPrice.setOriginalPrice(CurrencyUtil.add(cartPrice.getOriginalPrice(), CurrencyUtil.mul(cartSku.getOriginalPrice(), cartSku.getNum())));

        //购物车所有小计合
        cartPrice.setGoodsPrice(CurrencyUtil.add(cartPrice.getGoodsPrice(), cartSku.getSubtotal()));

        //购物车返现合
        cartPrice.setCashBack(CurrencyUtil.add(cartPrice.getCashBack(), CurrencyUtil.sub(CurrencyUtil.mul(cartSku.getOriginalPrice(), cartSku.getNum()), cartSku.getSubtotal())));

        //购物车使用积分
        cartPrice.setExchangePoint(cartPrice.getExchangePoint() + cartSku.getPoint());

        //购物车小计
        cartPrice.setTotalPrice(CurrencyUtil.add(cartPrice.getTotalPrice(), cartSku.getSubtotal()));

        //累计商品重量
        double weight = CurrencyUtil.mul(cartSku.getGoodsWeight(), cartSku.getNum());
        double cartWeight = CurrencyUtil.add(cart.getWeight(), weight);
        cart.setWeight(cartWeight);
    }

    /**
     * 计算优惠后商品单价
     *
     * @param cartSku       购物车商品sku
     * @param cost          优惠后价格
     * @param promotionType 优惠活动类型
     */
    private void calculatorPurchasePrice(CartSkuVO cartSku, Double cost, String promotionType) {
        if (PromotionTypeEnum.EXCHANGE.name().equals(promotionType) ||
                PromotionTypeEnum.GROUPBUY.name().equals(promotionType) ||
                PromotionTypeEnum.SECKILL.name().equals(promotionType) ||
                PromotionTypeEnum.MINUS.name().equals(promotionType)) {
            cartSku.setPurchasePrice(CurrencyUtil.div(cost, cartSku.getNum()));
        }
    }

    /**
     * 计算优惠券优惠价格
     *
     * @param price    价格对象
     * @param couponVO 优惠券对象
     */
    private void calculatorCoupon(PriceDetailVO price, CouponVO couponVO) {
        Double totalGroupPrice;
        if (couponVO != null) {
            String couponScript = this.scriptProcess.readCouponScript(couponVO.getCouponId());
            Map param = new HashedMap();
            param.put("$price", price.getTotalPrice());
            if (couponScript != null) {
                totalGroupPrice = scriptProcess.countPrice(couponScript, param);
                if (totalGroupPrice >= 0) {
                    price.setCouponPrice(CurrencyUtil.sub(price.getTotalPrice(), totalGroupPrice));
                    price.setTotalPrice(totalGroupPrice);
                }
            }
        }
    }


}
