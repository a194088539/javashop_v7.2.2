package com.enation.app.javashop.service.trade.cart.cartbuilder;

import com.enation.app.javashop.model.promotion.tool.vo.PromotionScriptVO;
import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;

import java.util.List;
import java.util.Map;

/**
*
* @description: 脚本处理
* @author: liuyulei
* @create: 2020/1/10 16:20
* @version:1.0
* @since:7.1.5
**/
public interface ScriptProcess {

    /**
     * 获取活动是否有效
     * @param script
     * @return  true：活动有效，false:活动无效
     */
    Boolean validTime(String script);

    /**
     * 获取促销赠品信息（包含赠送优惠券，赠送商品，赠送积分）
     * @param script
     * @param params
     */
    String giveGift(String script, Map<String,Object> params);

    /**
     * 获取需要使用多少积分兑换商品
     * @param script
     * @param params
     * @return
     */
    Integer countPoint(String script, Map<String,Object> params);

    /**
     * 计算促销优惠价格
     * @param script  脚本
     * @param params  参数
     * @return  优惠后的价格
     */
    Double countPrice(String script, Map<String,Object> params);

    /**
     * 获取购物车级别促销脚本信息
     * @param sellerId
     * @return
     */
    List<PromotionScriptVO> readCartScript(Long sellerId);

    /**
     * 获取sku级别促销脚本信息
     * @param skuList
     * @return
     */
    List<PromotionScriptVO> readSkuScript(List<CartSkuVO> skuList);

    /**
     * 获取sku促销活动信息
     * @param skuId 货品id
     * @return
     */
    List<PromotionScriptVO> readSkuScript(Long skuId);


    /**
     * 获取运费
     * @param script 脚本
     * @param params 参数
     * @return 运费
     */
    Double getShipPrice(String script, Map<String,Object> params);


    /**
     * 读取优惠券脚本
     * @param couponId 优惠券id
     * @return
     */
    String readCouponScript(Long couponId);

}
