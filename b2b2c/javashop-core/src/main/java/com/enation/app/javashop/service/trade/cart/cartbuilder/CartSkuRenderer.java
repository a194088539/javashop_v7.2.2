package com.enation.app.javashop.service.trade.cart.cartbuilder;

import com.enation.app.javashop.model.trade.cart.enums.CartType;
import com.enation.app.javashop.model.trade.cart.enums.CheckedWay;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.service.trade.cart.cartbuilder.impl.CartSkuFilter;

import java.util.List;

/**
 * 购物车sku渲染器，负责生产一个全新的cartList<br/>
 * 此步生产的物料是{@link com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo}
 * 文档请参考：<br>
 * <a href="http://doc.javamall.com.cn/current/achitecture/jia-gou/ding-dan/cart-and-checkout.html" >购物车架构</a>
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/11
 */
public interface CartSkuRenderer {

    /**
     * 渲染sku数据
     */
    void renderSku(List<CartVO> cartList, CartType cartType, CheckedWay way);

    /**
     * 渲染sku数据
     * 带过滤器式的
     */
    void renderSku(List<CartVO> cartList, CartSkuFilter cartFilter, CartType cartType, CheckedWay way);
}
