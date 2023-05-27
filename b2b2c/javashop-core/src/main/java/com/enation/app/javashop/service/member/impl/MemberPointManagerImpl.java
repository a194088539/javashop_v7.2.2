package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.enation.app.javashop.mapper.member.MemberMapper;
import com.enation.app.javashop.mapper.member.MemberPointHistoryMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberPointHistory;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberPointHistoryManager;
import com.enation.app.javashop.service.member.MemberPointManager;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

/**
 * 会员积分实现
 * 调用此接口需要完整填写每项属性
 *
 * @author zh
 * @version v7.0
 * @since v7.0
 * 2018-04-03 15:44:12
 */
@Service
public class MemberPointManagerImpl implements MemberPointManager {

    @Autowired
    private MemberManager memberManager;
    @Autowired
    private MemberPointHistoryManager memberPointHistoryManager;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MemberPointHistoryMapper memberPointHistoryMapper;

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void pointOperation(@Valid MemberPointHistory memberPointHistory) {
        //检测会员积分信息
        this.checkMemberPoint(memberPointHistory);
        //根据会员ID获取会员信息并校验
        Member member = memberManager.getModel(memberPointHistory.getMemberId());
        if (member == null) {
            throw new ResourceNotFoundException("此会员不存在");
        }
        //设置会员积分操作历史添加时间
        memberPointHistory.setTime(DateUtil.getDateline());
        //设置会员ID
        memberPointHistory.setMemberId(member.getMemberId());
        //会员积分操作历史信息入库
        memberPointHistoryManager.add(memberPointHistory);
        //如果等级积分为空，默认设置为0
        if (member.getGradePoint() == null) {
            member.setGradePoint(0L);
        }

        //新建修改条件包装器
        UpdateWrapper<Member> gradeWrapper = new UpdateWrapper<>();

        //如果会员等级积分类型为增加 1：增加，0：消费
        if (memberPointHistory.getGradePointType().equals(1)) {
            //设置增加等级积分
            gradeWrapper.setSql("grade_point=grade_point+" + memberPointHistory.getGradePoint());
        } else {
            //如果会员等级积分减去消费的等级积分大于0
            if (member.getGradePoint() - memberPointHistory.getGradePoint() > 0) {
                //设置减少等级积分
                gradeWrapper.setSql("grade_point=grade_point-" + memberPointHistory.getGradePoint());
            } else {
                //否则默认等级积分为0（不能为负数）
                gradeWrapper.set("grade_point", 0);
            }
        }

        //如果消费积分为空，默认设置为0
        if (member.getConsumPoint() == null) {
            member.setConsumPoint(0L);
        }

        //如果会员消费积分类型为增加 1：增加，0：消费
        if (memberPointHistory.getConsumPointType().equals(1)) {
            //设置增加消费积分
            gradeWrapper.setSql("consum_point=consum_point+" + memberPointHistory.getConsumPoint());
        } else {
            //如果会员消费积分减去消费的消费积分大于0
            if (member.getConsumPoint() - memberPointHistory.getConsumPoint() > 0) {
                //设置减少消费积分
                gradeWrapper.setSql("consum_point=consum_point-" + memberPointHistory.getConsumPoint());
            } else {
                //否则默认消费积分为0（不能为负数）
                gradeWrapper.set("consum_point", 0);
            }
        }
        //以会员ID为修改条件
        gradeWrapper.eq("member_id", member.getMemberId());
        //修改会员消费积分
        memberMapper.update(new Member(), gradeWrapper);

    }

    /**
     * 检测会员积分信息
     * @param memberPointHistory 会员积分信息
     * @return
     */
    private MemberPointHistory checkMemberPoint(MemberPointHistory memberPointHistory) {
        //对消费积分类型的处理
        Integer consumPointType = memberPointHistory.getConsumPointType();
        boolean bool = consumPointType == null || (consumPointType != 1 && consumPointType != 0);
        if (bool) {
            throw new ServiceException(MemberErrorCode.E106.code(), "消费积分类型不正确");
        }
        //对消费积分的处理
        Long consumPoint = memberPointHistory.getConsumPoint();
        bool = consumPoint == null || consumPoint < 0;
        if (bool) {
            throw new ServiceException(MemberErrorCode.E106.code(), "消费积分不正确");
        }
        //对等级积分类型的处理
        Integer gadePointType = memberPointHistory.getGradePointType();
        bool = gadePointType == null || (gadePointType != 1 && gadePointType != 0);
        if (bool) {
            throw new ServiceException(MemberErrorCode.E106.code(), "等级积分类型不正确");
        }
        //对消费积分的处理
        Integer gadePoint = memberPointHistory.getGradePoint();
        bool = gadePoint == null || gadePoint < 0;
        if (bool) {
            throw new ServiceException(MemberErrorCode.E106.code(), "等级积分不正确");
        }
        return memberPointHistory;

    }

    @Override
    public boolean checkPointSettingOpen() {
        return false;
    }
}
