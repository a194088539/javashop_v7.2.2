package com.enation.app.javashop.service.trade.cart.cartbuilder;

import com.enation.app.javashop.model.trade.cart.vo.CartVO;

import java.util.List;

/**
 * 购物车促销信息渲染器<br/>
 * 文档请参考：<br>
 * <a href="http://doc.javamall.com.cn/current/achitecture/jia-gou/ding-dan/cart-and-checkout.html" >购物车架构</a>
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/10
 */
public interface CartPromotionRenderer {


    /**
     * 对购物车进行渲染促销数据
     * @param cartList
     * @return
     */
    void render(List<CartVO> cartList);

}
