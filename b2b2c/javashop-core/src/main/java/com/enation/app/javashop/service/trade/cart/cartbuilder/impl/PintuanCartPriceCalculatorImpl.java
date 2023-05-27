package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;

import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.enation.app.javashop.service.trade.cart.cartbuilder.CartPriceCalculator;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kingapex on 2018/12/10.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/10
 */
@Service
public class PintuanCartPriceCalculatorImpl implements CartPriceCalculator {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public PriceDetailVO countPrice(List<CartVO> cartList, Boolean includeCoupon) {

        //根据规则计算价格
        PriceDetailVO priceDetailVO = this.countPriceWithRule(cartList);


        return priceDetailVO;
    }


    private PriceDetailVO countPriceWithRule(List<CartVO> cartList) {

        PriceDetailVO price = new PriceDetailVO();

        for (CartVO cart : cartList) {


            PriceDetailVO cartPrice = new PriceDetailVO();
            cartPrice.setFreightPrice(cart.getPrice().getFreightPrice());
            for (CartSkuVO cartSku : cart.getSkuList()) {


                //购物车全部商品的原价合
                cartPrice.setOriginalPrice(CurrencyUtil.add(cartPrice.getGoodsPrice(), cartSku.getSubtotal()));

                //购物车所有小计合
                cartPrice.setGoodsPrice(CurrencyUtil.add(cartPrice.getGoodsPrice(), cartSku.getSubtotal()));

                //累计商品重量
                double weight = CurrencyUtil.mul(cartSku.getGoodsWeight(), cartSku.getNum());
                double cartWeight = CurrencyUtil.add(cart.getWeight(), weight);
                cart.setWeight(cartWeight);


            }


            //总价为商品价加运费
            double totalPrice = CurrencyUtil.add(cartPrice.getGoodsPrice(), cartPrice.getFreightPrice());
            cartPrice.setTotalPrice(totalPrice);
            cart.setPrice(cartPrice);

            price = price.plus(cartPrice);


        }


        logger.debug("计算完优惠后购物车数据为：");
        logger.debug(cartList);

        logger.debug("价格为：");
        logger.debug(price);

        return price;
    }


}
