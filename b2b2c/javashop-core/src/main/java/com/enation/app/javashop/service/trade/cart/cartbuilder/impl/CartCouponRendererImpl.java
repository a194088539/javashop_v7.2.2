package com.enation.app.javashop.service.trade.cart.cartbuilder.impl;

import com.enation.app.javashop.client.member.MemberCouponClient;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.promotion.coupon.vo.CouponValidateResult;
import com.enation.app.javashop.model.trade.cart.vo.CartVO;
import com.enation.app.javashop.model.trade.cart.vo.CouponVO;
import com.enation.app.javashop.model.trade.cart.vo.SelectedPromotionVo;
import com.enation.app.javashop.service.trade.cart.CartPromotionManager;
import com.enation.app.javashop.service.trade.cart.cartbuilder.CartCouponRenderer;
import com.enation.app.javashop.service.trade.cart.util.CouponValidateUtil;
import com.enation.app.javashop.framework.context.user.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车优惠券渲染实现
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/12/18
 */
@Service
public class CartCouponRendererImpl implements CartCouponRenderer {


    @Autowired
    private MemberCouponClient memberCouponClient;

    @Autowired
    private CartPromotionManager cartPromotionManager;

    @Override
    public List<CouponVO> render(List<CartVO> cartList) {
        List<Long> sellerList = new ArrayList<>();

        //商家id
        cartList.forEach(cartVO -> {
            sellerList.add(cartVO.getSellerId());
        });
        Long[] sellerIds = new Long[sellerList.size()];

        //查询出这些店铺的所有优惠券(包括平台)
        List<MemberCoupon> couponList = this.memberCouponClient.listByCheckout(sellerList.toArray(sellerIds), UserContext.getBuyer().getUid());

        //填充购物车的优惠券列表
        return fillOneCartCoupon(cartList, couponList);

    }


    /**
     * 填充一个购物车的优惠劵
     *
     * @param cartList
     * @param couponList
     * @return
     */
    private List<CouponVO> fillOneCartCoupon(List<CartVO> cartList, List<MemberCoupon> couponList) {


        //如果购物车中包含积分商品，则无需渲染积分商品不能使用优惠券  add by liuyulei 2019-05-14
        SelectedPromotionVo selectedPromotionVo = cartPromotionManager.getSelectedPromotion();
        boolean isEnable = false;
        CouponVO selectedSiteCoupon = selectedPromotionVo.getCouponMap().get(0L);
        Map<Long, CouponVO> selectedCouponMap = new HashMap<>();
        for (CartVO cartVO : cartList) {
            isEnable = CouponValidateUtil.validateCoupon(selectedPromotionVo, cartVO.getSellerId(), cartVO.getSkuList());
            if (isEnable) {
                //存在积分商品
                break;
            }
            //查找可能已经选中的的优惠劵
            if (selectedSiteCoupon != null) {
                //选中了站点优惠券
                selectedCouponMap.put(0L, selectedSiteCoupon);
            }else{
                CouponVO selectedCoupon = selectedPromotionVo.getCouponMap().get(cartVO.getSellerId());
                if (selectedPromotionVo.getCouponMap().get(cartVO.getSellerId()) != null) {
                    selectedCouponMap.put(cartVO.getSellerId(), selectedCoupon);
                }
            }
        }

        //要形成的购物车优惠券列表
        List<CouponVO> cartCouponList = new ArrayList<>();
        //查看优惠券的范围
        for (MemberCoupon coupon : couponList) {

            CouponVO couponVO = new CouponVO(coupon);
            //判读是否存在积分商品，如果存在则不能使用优惠券
            if (isEnable) {
                couponVO.setEnable(0);
                couponVO.setErrorMsg("当前购物车内包含积分商品，不能使用优惠券！");
                couponVO.setSelected(0);
                continue;
            }
            //查看该优惠券是否可用
            CouponValidateResult enableRes = CouponValidateUtil.isEnable(coupon, cartList);
            if (enableRes.isEnable()) {
                //该优惠券可用
                couponVO.setEnable(1);
                //设置可以使用该优惠券的商品
                couponVO.setEnableSkuList(enableRes.getSkuIdList());
                //如果购物车存在优惠劵  当优惠券可用时才设置是否选中
                CouponVO selectedCoupon = selectedCouponMap.get(coupon.getSellerId());
                if (selectedCoupon != null && selectedCoupon.getMemberCouponId().equals(couponVO.getMemberCouponId())) {
                    couponVO.setSelected(1);
                } else {
                    couponVO.setSelected(0);
                }
            } else {
                couponVO.setEnable(0);
                couponVO.setErrorMsg("订单金额不满足此优惠券使用金额！");
            }
            cartCouponList.add(couponVO);
        }

        return cartCouponList;
    }





}
