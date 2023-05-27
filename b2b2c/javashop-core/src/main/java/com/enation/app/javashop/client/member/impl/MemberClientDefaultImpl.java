package com.enation.app.javashop.client.member.impl;

import com.enation.app.javashop.client.member.MemberClient;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberCoupon;
import com.enation.app.javashop.model.member.dos.MemberPointHistory;
import com.enation.app.javashop.model.member.vo.BackendMemberVO;
import com.enation.app.javashop.service.member.MemberCouponManager;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberPointManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员业务默认实现
 *
 * @author zh
 * @version v7.0
 * @date 18/7/27 上午11:52
 * @since v7.0
 */
@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class MemberClientDefaultImpl implements MemberClient {

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MemberPointManager memberPointManager;

    @Autowired
    private MemberCouponManager memberCouponManager;

    @Override
    public Member getModel(Long memberId) {
        return memberManager.getModel(memberId);
    }

    @Override
    public void loginNumToZero() {
        memberManager.loginNumToZero();
    }

    @Override
    public Member edit(Member member, Long id) {

        return memberManager.edit(member, id);
    }

    /**
     * 更新登录次数
     *
     * @param memberId
     * @param now
     * @return
     */
    @Override
    public void updateLoginNum(Long memberId, Long now) {
        memberManager.updateLoginNum(memberId, now);
    }

    @Override
    public void pointOperation(MemberPointHistory memberPointHistory) {

        memberPointManager.pointOperation(memberPointHistory);
    }

    @Override
    public List<String> queryAllMemberIds() {

        return memberManager.queryAllMemberIds();
    }

    @Override
    public MemberCoupon getModel(Long memberId, Long mcId) {
        return memberCouponManager.getModel(memberId, mcId);
    }

    @Override
    public void usedCoupon(Long mcId, String orderSn) {
        memberCouponManager.usedCoupon(mcId, orderSn);
    }

    @Override
    public void receiveBonus(Long memberId, String memberName, Long couponId) {

        memberCouponManager.receiveBonus(memberId, memberName, couponId);

    }

    @Override
    public List<BackendMemberVO> newMember(Integer length) {
        return memberManager.newMember(length);
    }
}
