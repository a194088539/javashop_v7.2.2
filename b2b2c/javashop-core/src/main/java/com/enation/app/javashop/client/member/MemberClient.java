package com.enation.app.javashop.client.member;

import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.member.dos.MemberPointHistory;
import com.enation.app.javashop.model.member.vo.BackendMemberVO;

import java.util.List;

/**
 * 会员客户户端
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 上午11:48
 * @since v7.0
 */

public interface MemberClient {
    /**
     * 根据会员id获取会员信息
     *
     * @param memberId
     * @return
     */
    Member getModel(Long memberId);

    /**
     * 登录数清零
     */
    void loginNumToZero();

    /**
     * 修改会员
     *
     * @param member 会员
     * @param id     会员主键
     * @return Member 会员
     */
    Member edit(Member member, Long id);

    /**
     * 更新登录次数
     *
     * @param memberId
     * @param now
     * @return
     */
    void updateLoginNum(Long memberId, Long now);

    /**
     * 会员积分操作，这个方法可同时进行添加积分和消费积分
     * 1、添加积分
     * gadePointType 为1则为添加等级积分  gadePoint为积分值
     * consumPointType 为1则为添加消费积分  consumPoint为消费积分值
     * <p>
     * 2、消费积分
     * gadePointType 为0则为消费等级积分  gadePoint为积分值
     * consumPointType 为0则为消费消费积分  consumPoint为消费积分值
     * <p>
     * 如果没有操作则gadePointType 为0 gadePointType为1
     * 如果没有操作则consumPoint 为0 consumPointType为1
     *
     * @param memberPointHistory 会员积分
     */
    void pointOperation(MemberPointHistory memberPointHistory);

    /**
     * 查询所有的会员id
     *
     * @return
     */
    List<String> queryAllMemberIds();

    /**
     * 读取我的优惠券
     *
     * @param memberId
     * @param mcId
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
     * 领取优惠券
     *
     * @param memberId   会员id
     * @param memberName 会员名称
     * @param couponId   优惠券id
     */
    void receiveBonus(Long memberId, String memberName, Long couponId);

    /**
     * 查询新的会员
     *
     * @param length
     * @return
     */
    List<BackendMemberVO> newMember(Integer length);

}
