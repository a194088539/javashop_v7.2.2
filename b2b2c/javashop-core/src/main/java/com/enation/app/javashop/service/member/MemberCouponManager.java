package com.enation.app.javashop.service.member;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.member.dto.MemberCouponQueryParam;
import com.enation.app.javashop.model.member.vo.MemberCouponNumVO;

import java.util.List;

/**
 * 会员优惠券
 *
 * @author Snow create in 2018/5/24
 * @version v2.0
 * @since v7.0.0
 */
public interface MemberCouponManager {

    /**
     * 领取优惠券
     * @param memberId 会员id
     * @param memberName 会员名称
     * @param couponId 优惠券id
     */
    void receiveBonus(Long memberId, String memberName, Long couponId);


    /**
     * 查询我的所有优惠券
     *
     * @param param 搜索参数
     * @return
     */
    WebPage list(MemberCouponQueryParam param);


    /**
     * 读取我的优惠券
     *
     * @param memberId 会员ID
     * @param mcId 优惠券ID
     * @return
     */
    MemberCoupon getModel(Long memberId, Long mcId);

    /**
     * 修改优惠券的使用信息
     *
     * @param mcId    优惠券id
     * @param orderSn 使用优惠券的订单编号
     */
    void usedCoupon(Long mcId, String orderSn);


    /**
     * 检测已领取数量
     *
     * @param couponId 会员优惠券ID
     */
    void checkLimitNum(Long couponId);

    /**
     * 结算页—查询会员优惠券
     *
     * @param sellerIds 商家id
     * @param memberId  会员id
     * @return
     */
    List<MemberCoupon> listByCheckout(Long[] sellerIds, Long memberId);

    /**
     * 优惠券各个状态数量
     *
     * @return
     */
    MemberCouponNumVO statusNum();


    /**
     * 查询某优惠券的领取列表
     *
     * @param couponId 会员优惠券ID
     * @param pageNo 页数
     * @param pageSize 每页数量
     * @return
     */
    WebPage queryByCouponId(Long couponId, Long pageNo, Long pageSize);

    /**
     * 取消某个会员优惠券
     * @param memberCouponId 会员优惠券ID
     */
    void cancel(Long memberCouponId);

    /**
     *  更新优惠券的店铺名称
     * @param sellerId 商家ID
     * @param sellerName 商家名称
     */
    void updateSellerName(Long sellerId, String sellerName);
}
