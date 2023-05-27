package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.mapper.member.AskReplyMapper;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.message.AskReplyMessage;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.goods.dto.GoodsSettingVO;
import com.enation.app.javashop.service.member.AskMessageManager;
import com.enation.app.javashop.service.member.AskReplyManager;
import com.enation.app.javashop.service.member.MemberAskManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.AskReplyDO;
import com.enation.app.javashop.model.member.dos.MemberAsk;
import com.enation.app.javashop.model.member.dto.ReplyQueryParam;
import com.enation.app.javashop.model.member.enums.AskMsgTypeEnum;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.model.member.enums.CommonStatusEnum;
import com.enation.app.javashop.model.member.vo.AskReplyVO;
import com.enation.app.javashop.model.member.vo.BatchAuditVO;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Admin;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员回复商品咨询业务接口实现
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-16
 */
@Service
public class AskReplyManagerImpl implements AskReplyManager {

    @Autowired
    private SettingClient settingClient;

    @Autowired
    private MemberAskManager memberAskManager;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private AskMessageManager askMessageManager;

    @Autowired
    private AskReplyMapper askReplyMapper;

    @Override
    public WebPage list(ReplyQueryParam param) {
        //新建查询条件包装器
        QueryWrapper<AskReplyDO> wrapper = new QueryWrapper<>();
        //删除状态为正常未删除
        wrapper.eq("is_del", DeleteStatusEnum.NORMAL.value());
        //如果会员商品咨询ID不为空，则将会员商品咨询ID作为查询条件
        wrapper.eq(param.getAskId() != null, "ask_id", param.getAskId());
        //如果查询关键字不为空，则将咨询回复内容或会员名称进行模糊匹配
        wrapper.and(StringUtil.notEmpty(param.getKeyword()), ew -> {
            ew.like("content", param.getKeyword()).or().like("member_name", param.getKeyword());
        });
        //如果会员名称不为空，则将会员名称进行模糊匹配
        wrapper.like(StringUtil.notEmpty(param.getMemberName()), "member_name", param.getKeyword());
        //如果回复内容不为空，则将回复内容进行模糊匹配
        wrapper.like(StringUtil.notEmpty(param.getContent()), "content", param.getKeyword());
        //如果审核状态不为空，则将审核状态作为查询条件
        wrapper.eq(StringUtil.notEmpty(param.getAuthStatus()), "auth_status", param.getAuthStatus());
        //如果回复时间-起始时间不为空并且不等于0，按回复时间-起始时间查询
        wrapper.ge(param.getStartTime() != null && param.getStartTime() != 0, "reply_time", param.getStartTime());
        //如果回复时间-结束时间不为空并且不等于0，按回复时间-结束时间查询
        wrapper.le(param.getEndTime() != null && param.getEndTime() != 0, "reply_time", param.getEndTime());
        //如果匿名状态不为空，按匿名状态查询
        wrapper.eq(StringUtil.notEmpty(param.getAnonymous()), "anonymous", param.getAnonymous());
        //如果回复状态不为空，按回复状态查询
        wrapper.eq(StringUtil.notEmpty(param.getReplyStatus()), "reply_status", param.getReplyStatus());
        //如果会员id不为空，按会员id查询
        wrapper.eq(param.getMemberId() != null, "member_id", param.getMemberId());
        //如果回复id不为空，排除某条回复（一般用于商品详情页面获取咨询回复）
        wrapper.eq(param.getReplyId() != null, "id", param.getReplyId());
        //按回复时间倒序排序
        wrapper.orderByDesc("reply_time");
        //获取分页数据
        IPage<AskReplyDO> iPage = askReplyMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    public WebPage listMemberReply(ReplyQueryParam param) {
        //获取会员问题咨询回复列表
        IPage<AskReplyVO> iPage = askReplyMapper.listMemberReply(new Page(param.getPageNo(), param.getPageSize()), param, DeleteStatusEnum.NORMAL.value());
        return PageConvert.convert(iPage);
    }

    @Override
    public void add(AskReplyDO askReplyDO) {
        askReplyMapper.insert(askReplyDO);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AskReplyDO updateReply(Long askId, String replyContent, String anonymous) {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();

        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }

        if (askId == null || askId == 0) {
            throw new ServiceException(MemberErrorCode.E107.code(), "问题咨询ID参数值不正确");
        }

        if (StringUtil.isEmpty(replyContent)) {
            throw new ServiceException(MemberErrorCode.E202.code(), "回复内容不能为空");
        }

        if (replyContent.length() < 3 && replyContent.length() > 120) {
            throw new ServiceException(MemberErrorCode.E202.code(), "回复内容应在3到120个字符之间");
        }

        if (!CommonStatusEnum.YES.value().equals(anonymous) && !CommonStatusEnum.NO.value().equals(anonymous)) {
            throw new ServiceException(MemberErrorCode.E107.code(), "是否匿名参数值不正确");
        }

        MemberAsk memberAsk = this.memberAskManager.getModel(askId);
        if (memberAsk == null) {
            throw new ServiceException(MemberErrorCode.E202.code(), "会员商品咨询信息不存在");
        }

        //获取商品咨询设置
        String json = this.settingClient.get(SettingGroup.GOODS);
        GoodsSettingVO goodsSettingVO = JsonUtil.jsonToObject(json, GoodsSettingVO.class);

        AskReplyDO askReplyDO = this.getModel(askId, buyer.getUid());

        if (askReplyDO == null || buyer.getUid().intValue() != askReplyDO.getMemberId().intValue()) {
            throw new ServiceException(MemberErrorCode.E200.code(), "无权回复");
        }

        if (CommonStatusEnum.YES.value().equals(askReplyDO.getReplyStatus())) {
            throw new ServiceException(MemberErrorCode.E202.code(), "您已回复过问题，不可重复回复");
        }

        //设置会员咨询回复要修改的信息
        askReplyDO.setMemberName(CommonStatusEnum.YES.value().equals(anonymous) ? "匿名" : buyer.getUsername());
        askReplyDO.setContent(replyContent);
        askReplyDO.setReplyTime(DateUtil.getDateline());
        askReplyDO.setAnonymous(anonymous);
        askReplyDO.setAuthStatus(goodsSettingVO.getAskAuth().intValue() == 0 ? AuditEnum.PASS_AUDIT.name() : AuditEnum.WAIT_AUDIT.name());
        askReplyDO.setReplyStatus(CommonStatusEnum.YES.value());
        //修改会员咨询回复相关信息
        askReplyMapper.updateById(askReplyDO);

        //如果平台没有开启会员咨询审核，那么就直接发送消息
        if (goodsSettingVO.getAskAuth().intValue() == 0) {
            List<AskReplyDO> list = new ArrayList<>();
            list.add(askReplyDO);

            AskReplyMessage askReplyMessage = new AskReplyMessage(list, memberAsk, DateUtil.getDateline());
            this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_GOODS_ASK_REPLY, AmqpExchange.MEMBER_GOODS_ASK_REPLY + "_ROUTING", askReplyMessage));

            //修改会员商品咨询回复数量
            this.memberAskManager.updateReplyNum(askId, 1);
        }
        return askReplyDO;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        AskReplyDO askReplyDO = this.getModel(id);
        if (askReplyDO == null) {
            throw new ServiceException(MemberErrorCode.E202.code(), "当前会员商品咨询回复已经删除");
        }
        //设置会员商品咨询回复状态为已删除
        askReplyDO.setIsDel(DeleteStatusEnum.DELETED.value());
        //修改会员商品咨询回复状态
        askReplyMapper.updateById(askReplyDO);

        //修改会员商品咨询回复数量
        this.memberAskManager.updateReplyNum(askReplyDO.getAskId(), -1);
        //删除消息数据
        this.askMessageManager.delete(askReplyDO.getAskId(), askReplyDO.getMemberId(), AskMsgTypeEnum.ASK.value());
    }

    @Override
    public void deleteByAskId(Long askId) {
        //新建修改条件包装器
        UpdateWrapper<AskReplyDO> wrapper = new UpdateWrapper<>();
        //设置修改字段-删除状态为已删除
        wrapper.set("is_del", DeleteStatusEnum.DELETED.value());
        //以会员商品咨询ID为修改条件
        wrapper.eq("ask_id", askId);
        //将会员商品咨询回复状态改为已删除
        askReplyMapper.update(new AskReplyDO(), wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchAudit(BatchAuditVO batchAuditVO) {
        //校验是否有管理端权限
        Admin admin = AdminUserContext.getAdmin();
        if (admin == null) {
            throw new NoPermissionException("没有权限审核会员咨询回复信息!");
        }
        //校验是否选择了要进行审核的咨询回复信息
        if (batchAuditVO.getIds() == null || batchAuditVO.getIds().length == 0) {
            throw new ServiceException(MemberErrorCode.E107.code(), "请选择要进行审核的会员商品咨询回复");
        }
        //校验咨询回复状态参数是否正确
        if (!AuditEnum.PASS_AUDIT.value().equals(batchAuditVO.getAuthStatus()) && !AuditEnum.REFUSE_AUDIT.value().equals(batchAuditVO.getAuthStatus())) {
            throw new ServiceException(MemberErrorCode.E107.code(), "审核状态参数值不正确");
        }
        //新建查询条件包装器
        QueryWrapper<AskReplyDO> wrapper = new QueryWrapper<>();
        //以咨询回复ID为查询条件
        wrapper.in("id", batchAuditVO.getIds());
        //获取咨询回复集合
        List<AskReplyDO> askReplyDOList = askReplyMapper.selectList(wrapper);
        //循环进行判断以及修改操作
        for (AskReplyDO askReplyDO : askReplyDOList) {
            //判断是否可以进行审核
            if (!AuditEnum.WAIT_AUDIT.value().equals(askReplyDO.getAuthStatus())) {
                throw new ServiceException(MemberErrorCode.E107.code(), "内容为【" + askReplyDO.getContent() + "】的回复不是可以进行审核的状态");
            }
            //新建问题咨询回复对象
            AskReplyDO askReply = new AskReplyDO();
            //设置要修改的字段-审核状态
            askReplyDO.setAuthStatus(batchAuditVO.getAuthStatus());
            //修改字段值
            askReplyMapper.updateById(askReplyDO);
        }

        //获取会员商品咨询ID
        Long askId = askReplyDOList.get(0).getAskId();

        //发送消息
        AskReplyMessage askReplyMessage = new AskReplyMessage(askReplyDOList, this.memberAskManager.getModel(askId), DateUtil.getDateline());
        this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_GOODS_ASK_REPLY, AmqpExchange.MEMBER_GOODS_ASK_REPLY + "_ROUTING", askReplyMessage));

        //修改会员商品咨询回复数量
        this.memberAskManager.updateReplyNum(askId, askReplyDOList.size());
    }

    @Override
    public AskReplyDO getModel(Long id) {
        return askReplyMapper.selectById(id);
    }

    @Override
    public AskReplyDO getModel(Long askId, Long memberId) {
        //新建查询条件包装器
        QueryWrapper<AskReplyDO> wrapper = new QueryWrapper<>();
        //以问题咨询ID为查询条件
        wrapper.eq("ask_id", askId);
        //以会员ID为查询条件
        wrapper.eq("member_id", memberId);
        return askReplyMapper.selectOne(wrapper);
    }

    @Override
    public AskReplyDO getNewestModel(Long askId) {
        //构建查询参数信息
        Map param = new HashMap();
        param.put("ask_id", askId);
        param.put("reply_status", CommonStatusEnum.YES.value());
        param.put("is_del", DeleteStatusEnum.NORMAL.value());
        param.put("auth_status", AuditEnum.PASS_AUDIT.value());
        return askReplyMapper.getNewestModel(param);
    }
}
