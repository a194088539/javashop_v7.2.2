package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;

import java.util.List;
import java.util.Map;

/**
 * 运费计算业务层接口
 *
 * @author Snow create in 2018/4/8
 * @version v2.0
 * @since v7.0.0
 */
public interface ShippingManager {

    /**
     * 获取各个商家的运费
     *
     * @param cartList 购物车
     * @param areaId   地区
     * @return
     */
    Map<Long, Double> getShippingPrice(List<CartVO> cartList, Long areaId);

    /**
     * 设置运费
     *
     * @param cartList 购物车集合
     */
    void setShippingPrice(List<CartVO> cartList);

    /**
     * 检测是否有不能配送的区域
     *
     * @param cartList 购物车
     * @param areaId   地区
     * @return
     */
    List<CacheGoods> checkArea(List<CartVO> cartList, Long areaId);


}
