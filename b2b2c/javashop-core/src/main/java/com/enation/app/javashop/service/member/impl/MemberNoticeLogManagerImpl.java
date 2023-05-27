package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.SqlSplicingUtil;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.AskMessageMapper;
import com.enation.app.javashop.mapper.member.MemberNoticeLogMapper;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.AskMessageDO;
import com.enation.app.javashop.model.member.dos.MemberNoticeLog;
import com.enation.app.javashop.model.member.dto.MemberNoticeDTO;
import com.enation.app.javashop.model.member.enums.CommonStatusEnum;
import com.enation.app.javashop.service.member.MemberNoticeLogManager;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员站内消息历史业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-07-05 14:10:16
 */
@Service
public class MemberNoticeLogManagerImpl implements MemberNoticeLogManager {

    @Autowired
    private MemberNoticeLogMapper memberNoticeLogMapper;

    @Autowired
    private AskMessageMapper askMessageMapper;

    @Override
    public WebPage list(long page, long pageSize, Integer read) {
        //新建查询条件包装器
        QueryWrapper<MemberNoticeLog> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //以删除状态是未删除作为查询条件
        wrapper.eq("is_del", 1);
        //校验是否已读参数是否为空，不为空则加入查询条件进行查询
        wrapper.eq(read != null, "is_read", read);
        //以消息发送时间倒序排序
        wrapper.orderByDesc("send_time");
        //获取站内消息分页列表数据
        IPage<MemberNoticeLog> iPage = memberNoticeLogMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberNoticeLog add(String content, long sendTime, Long memberId, String title) {
        //新建会员站内消息对象
        MemberNoticeLog memberNoticeLog = new MemberNoticeLog();
        //设置消息内容
        memberNoticeLog.setContent(content);
        //设置会员ID
        memberNoticeLog.setMemberId(memberId);
        //设置消息标题
        memberNoticeLog.setTitle(title);
        //是否删除默认正常状态 0：删除，1：正常
        memberNoticeLog.setIsDel(1);
        //是否已读默认未读 0：未读，1：已读
        memberNoticeLog.setIsRead(0);
        //设置消息发送时间
        memberNoticeLog.setSendTime(sendTime);
        //设置消息接收时间
        memberNoticeLog.setReceiveTime(DateUtil.getDateline());
        //会员站内消息入库
        memberNoticeLogMapper.insert(memberNoticeLog);
        return memberNoticeLog;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int adds(String content, long sendTime, List<String> memberIds, String title) {
        for (int i = 0; i < memberIds.size(); i++) {
            MemberNoticeLog memberNoticeLog = new MemberNoticeLog();
            memberNoticeLog.setContent(content);
            memberNoticeLog.setTitle(title);
            //是否删除默认正常状态
            memberNoticeLog.setIsDel(1);
            //是否已读默认未读
            memberNoticeLog.setIsRead(0);
            memberNoticeLog.setSendTime(sendTime);
            memberNoticeLog.setReceiveTime(DateUtil.getDateline());
            if (memberIds.get(i) != null) {
                memberNoticeLog.setMemberId(Long.parseLong(memberIds.get(i)));
            }
            this.memberNoticeLogMapper.insert(memberNoticeLog);
        }
        return 1;
    }

    @Override
    public void read(Long[] ids) {
        //新建修改条件包装器
        UpdateWrapper<MemberNoticeLog> wrapper = new UpdateWrapper<>();
        //设置消息为已读状态 0：未读，1：已读
        wrapper.set("is_read", 1);
        //以消息ID为修改条件
        wrapper.in("id", ids);
        //以会员ID为修改条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //修改消息内容
        memberNoticeLogMapper.update(new MemberNoticeLog(), wrapper);
    }

    @Override
    public void delete(Long[] ids) {
        //新建修改条件包装器
        UpdateWrapper<MemberNoticeLog> wrapper = new UpdateWrapper<>();
        //设置消息为删除状态 0：删除，1：正常
        wrapper.set("is_del", 0);
        //以消息ID为修改条件
        wrapper.in("id", ids);
        //以会员ID为修改条件
        wrapper.eq("member_id", UserContext.getBuyer().getUid());
        //修改消息内容
        memberNoticeLogMapper.update(new MemberNoticeLog(), wrapper);
    }

    @Override
    public MemberNoticeDTO getNum() {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }

        //新建查询条件包装器
        QueryWrapper<MemberNoticeLog> memberNoticeWrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        memberNoticeWrapper.eq("member_id", buyer.getUid());
        //以消息状态是正常状态为查询条件
        memberNoticeWrapper.eq("is_del", 1);
        //以消息是未读状态为查询条件
        memberNoticeWrapper.eq("is_read", 0);
        //获取系统未读消息数量
        int systemNum = memberNoticeLogMapper.selectCount(memberNoticeWrapper);

        //新建查询条件包装器
        QueryWrapper<AskMessageDO> askMessageWrapper = new QueryWrapper<>();
        //以消息状态是正常状态为查询条件
        askMessageWrapper.eq("is_del", DeleteStatusEnum.NORMAL.value());
        //以会员ID为查询条件
        askMessageWrapper.eq("member_id", buyer.getUid());
        //以消息是未读状态为查询条件
        askMessageWrapper.eq("is_read", CommonStatusEnum.NO.value());
        //获取问答未读消息数量
        int askNum = askMessageMapper.selectCount(askMessageWrapper);

        //未读消息总数 = 系统未读消息数 + 问答未读消息数
        int total = systemNum + askNum;

        //信息会员站内消息对象DTO
        MemberNoticeDTO memberNoticeDTO = new MemberNoticeDTO();
        //设置系统未读消息数
        memberNoticeDTO.setSystemNum(systemNum);
        //设置问答未读消息数
        memberNoticeDTO.setAskNum(askNum);
        //设置未读消息总数
        memberNoticeDTO.setTotal(total);
        return memberNoticeDTO;
    }
}
