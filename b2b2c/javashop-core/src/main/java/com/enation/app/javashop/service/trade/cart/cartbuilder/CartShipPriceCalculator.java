package com.enation.app.javashop.service.trade.cart.cartbuilder;

import com.enation.app.javashop.model.trade.cart.vo.CartVO;

import java.util.List;

/**
 * Created by kingapex on 2018/12/19.
 * 运费计算器
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/19
 */
public interface CartShipPriceCalculator  {

    void countShipPrice(List<CartVO>cartList) ;

}
