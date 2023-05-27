package com.enation.app.javashop.service.trade.cart;

import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.trade.cart.vo.CartPromotionVo;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.model.trade.cart.vo.SelectedPromotionVo;

import java.util.List;

/**
 * 购物车优惠信息处理接口<br/>
 * 负责促销的使用、取消、读取。
 * 文档请参考：<br>
 * <a href="http://doc.javamall.com.cn/current/achitecture/jia-gou/ding-dan/cart-and-checkout.html" >购物车架构</a>
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/1
 */
public interface CartPromotionManager {


    SelectedPromotionVo getSelectedPromotion();



    /**
     * 使用一个促销活动
     *
     * @param sellerId
     * @param skuId
     * @param promotionVo
     */
    void usePromotion(Long sellerId, Long skuId, CartPromotionVo promotionVo);


    /**
     * 使用一个优惠券
     *
     * @param sellerId
     * @param mcId
     * @param cartList
     */
    void useCoupon(Long sellerId, Long mcId, List<CartVO> cartList,MemberCoupon memberCoupon);

    /**
     * 检测一个优惠券
     *
     * @param sellerId
     * @param mcId
     * @param cartList
     */
    MemberCoupon detectCoupon(Long sellerId, Long mcId, List<CartVO> cartList);

    /**
     * 删除一个店铺优惠券的使用
     *
     * @param sellerId
     */
    void deleteCoupon(Long sellerId);

    /**
     * 清除一个所有的优惠券
     */
    void cleanCoupon();

    /**
     * 批量删除sku对应的优惠活动
     *
     * @param skuids
     */
    void delete(Long[] skuids);

    /**
     * 根据sku检查并清除无效的优惠活动
     *
     * @param skuId
     * @param promotionId
     * @param sellerId
     * @param promotionType
     * @return
     */
    boolean checkPromotionInvalid(Long skuId, Long promotionId, Long sellerId, String promotionType);


    /**
     * 清空当前用户的所有优惠活动
     */
    void clean();

    /**
     * 检测并清除无效活动
     */
    void checkPromotionInvalid();
}
