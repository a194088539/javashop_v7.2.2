package com.enation.app.javashop.service.trade.pintuan;

import com.enation.app.javashop.model.trade.cart.vo.CartSkuOriginVo;
import com.enation.app.javashop.model.trade.cart.vo.CartView;

/**
 * Created by kingapex on 2019-01-23.
 * 拼团购物车业务类接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-23
 */
public interface PintuanCartManager  {


    /**
     * 获取拼团购物车
     * @return 购物车视图
     */
    CartView  getCart();


    /**
     * 将一个拼团的sku加入到购物车中
     * @param skuId
     * @param  num 加入的数量
     */
    CartSkuOriginVo addSku(Long skuId, Integer num);




}
