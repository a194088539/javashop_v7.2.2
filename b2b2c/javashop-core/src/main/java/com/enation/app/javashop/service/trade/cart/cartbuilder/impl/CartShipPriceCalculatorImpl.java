package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;

import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.service.trade.cart.cartbuilder.CartShipPriceCalculator;
import com.enation.app.javashop.service.trade.order.ShippingManager;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/19
 */
@Service
public class CartShipPriceCalculatorImpl implements CartShipPriceCalculator {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ShippingManager shippingManager;

    @Override
    public void countShipPrice(List<CartVO> cartList) {
        shippingManager.setShippingPrice(cartList);

        logger.debug("购物车处理运费结果为：");
        logger.debug(cartList);
    }

}
