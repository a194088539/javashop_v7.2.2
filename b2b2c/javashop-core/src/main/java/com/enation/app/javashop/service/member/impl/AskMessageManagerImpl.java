package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.AskMessageMapper;
import com.enation.app.javashop.service.member.AskMessageManager;
import com.enation.app.javashop.service.member.AskReplyManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.AskMessageDO;
import com.enation.app.javashop.model.member.dos.AskReplyDO;
import com.enation.app.javashop.model.member.enums.AskMsgTypeEnum;
import com.enation.app.javashop.model.member.enums.CommonStatusEnum;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员商品咨询消息业务接口实现
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-16
 */
@Service
public class AskMessageManagerImpl implements AskMessageManager {

    @Autowired
    private AskReplyManager askReplyManager;

    @Autowired
    private AskMessageMapper askMessageMapper;

    @Override
    public WebPage list(Long pageNo, Long pageSize, String isRead) {
        //获取当前登录的用户信息
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }
        //新建查询包装器
        QueryWrapper<AskMessageDO> wrapper = new QueryWrapper<>();
        //查询状态为正常的消息
        wrapper.eq("is_del", DeleteStatusEnum.NORMAL.value());
        //将会员ID设为查询条件
        wrapper.eq("member_id", buyer.getUid());
        //如果读取状态不为空，将读取状态设为查询条件
        wrapper.eq(StringUtil.notEmpty(isRead), "is_read", isRead);
        //以接收消息时间倒序查询
        wrapper.orderByDesc("receive_time");
        //获取分页列表数据
        IPage<AskMessageDO> iPage = askMessageMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public void read(Long[] ids) {
        //获取当前登录的用户信息
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }
        //新建修改包装器
        UpdateWrapper<AskMessageDO> wrapper = new UpdateWrapper<>();
        //设置修改内容-读取状态改为已读
        wrapper.set("is_read", CommonStatusEnum.YES.value());
        //以消息ID为修改条件
        wrapper.in("id", ids);
        //以会员ID为修改条件
        wrapper.eq("member_id", buyer.getUid());
        //将消息修改为已读状态
        askMessageMapper.update(new AskMessageDO(), wrapper);
    }

    @Override
    public void delete(Long[] ids) {
        //获取当前登录的用户信息
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }
        //新建修改包装器
        UpdateWrapper<AskMessageDO> wrapper = new UpdateWrapper<>();
        //设置修改内容-删除状态改为已删除
        wrapper.set("is_del", DeleteStatusEnum.DELETED.value());
        //以消息ID为修改条件
        wrapper.in("id", ids);
        //以会员ID为修改条件
        wrapper.eq("member_id", buyer.getUid());
        //将消息修改为删除状态
        askMessageMapper.update(new AskMessageDO(), wrapper);
    }

    @Override
    public void delete(Long askId, Long memberId, String msgType) {
        //新建修改包装器
        UpdateWrapper<AskMessageDO> wrapper = new UpdateWrapper<>();
        //设置修改内容-删除状态改为已删除
        wrapper.set("is_del", DeleteStatusEnum.DELETED.value());
        //以问题咨询ID为修改条件
        wrapper.eq("ask_id", askId);
        //以会员ID为修改条件
        wrapper.eq("member_id", memberId);
        //以消息类型为提问消息为修改条件
        wrapper.eq("msg_type", AskMsgTypeEnum.ASK.value());
        //将消息修改为删除状态
        askMessageMapper.update(new AskMessageDO(), wrapper);
    }

    @Override
    public void deleteByAskId(Long askId) {
        //新建修改包装器
        UpdateWrapper<AskMessageDO> wrapper = new UpdateWrapper<>();
        //设置修改内容-删除状态改为已删除
        wrapper.set("is_del", DeleteStatusEnum.DELETED.value());
        //以问题咨询ID为修改条件
        wrapper.eq("ask_id", askId);
        //将消息修改为删除状态
        askMessageMapper.update(new AskMessageDO(), wrapper);
    }

    @Override
    public AskMessageDO getModel(Long id) {
        return askMessageMapper.selectById(id);
    }

    @Override
    public void addAskMessage(AskMessageDO askMessageDO) {
        //提问消息入库
        askMessageDO.setId(null);
        askMessageMapper.insert(askMessageDO);

        //如果是消息类型为提问消息，那么需要给接收消息的会员在回复表中添加一条未回复的信息
        if (AskMsgTypeEnum.ASK.value().equals(askMessageDO.getMsgType())) {
            AskReplyDO askReplyDO = new AskReplyDO();
            askReplyDO.setAskId(askMessageDO.getAskId());
            askReplyDO.setMemberId(askMessageDO.getMemberId());
            askReplyDO.setIsDel(DeleteStatusEnum.NORMAL.value());
            askReplyDO.setReplyStatus(CommonStatusEnum.NO.value());
            askReplyDO.setCreateTime(DateUtil.getDateline());
            this.askReplyManager.add(askReplyDO);
        }

    }
}
