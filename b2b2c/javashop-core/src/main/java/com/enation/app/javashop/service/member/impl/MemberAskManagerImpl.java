package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.member.MemberAskMapper;
import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.base.SettingGroup;
import com.enation.app.javashop.model.base.message.MemberAskMessage;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.goods.GoodsClient;
import com.enation.app.javashop.client.system.SettingClient;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsSettingVO;
import com.enation.app.javashop.model.goods.vo.CacheGoods;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.service.member.AskReplyManager;
import com.enation.app.javashop.service.member.MemberAskManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dos.MemberAsk;
import com.enation.app.javashop.model.member.dto.AskQueryParam;
import com.enation.app.javashop.model.member.enums.AuditEnum;
import com.enation.app.javashop.model.member.enums.CommonStatusEnum;
import com.enation.app.javashop.model.member.vo.BatchAuditVO;
import com.enation.app.javashop.model.member.vo.MemberAskVO;
import com.enation.app.javashop.service.member.AskMessageManager;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.system.enums.DeleteStatusEnum;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Admin;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Seller;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 咨询业务类
 *
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-04 17:41:18
 */
@Service
public class MemberAskManagerImpl implements MemberAskManager {

    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private SettingClient settingClient;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private AskReplyManager askReplyManager;
    @Autowired
    private AskMessageManager askMessageManager;
    @Autowired
    private MemberAskMapper memberAskMapper;

    @Override
    public WebPage list(AskQueryParam param) {
        //构造查询条件包装器
        QueryWrapper<MemberAsk> wrapper = new QueryWrapper<>();
        //按状态查询
        wrapper.eq("status", DeleteStatusEnum.NORMAL.value());
        //如果查询关键字不为空，以咨询内容或商品名称或会员名称作为条件进行模糊查询
        wrapper.and(StringUtil.notEmpty(param.getKeyword()), eq -> {
            eq.like("content", param.getKeyword()).or().like("goods_name", param.getKeyword()).or().like("member_name", param.getKeyword());
        });
        //如果会员ID不为空，以会员ID为条件查询
        wrapper.eq(param.getMemberId() != null, "member_id", param.getMemberId());
        //如果商家回复状态不为空，以商家回复状态为条件查询
        wrapper.eq(StringUtil.notEmpty(param.getReplyStatus()), "reply_status", param.getReplyStatus());
        //如果商家ID不为空并且不等于0，以商家ID为条件查询
        wrapper.eq(param.getSellerId() != null && param.getSellerId() != 0, "seller_id", param.getSellerId());
        //如果商品名称不为空，以商品名称为条件模糊查询
        wrapper.like(StringUtil.notEmpty(param.getGoodsName()), "goods_name", param.getGoodsName());
        //如果会员名称不为空，以会员名称为条件模糊查询
        wrapper.like(StringUtil.notEmpty(param.getMemberName()), "member_name", param.getMemberName());
        //如果咨询内容不为空，以咨询内容为条件模糊查询
        wrapper.like(StringUtil.notEmpty(param.getContent()), "content", param.getContent());
        //如果审核状态不为空，以审核状态为条件查询
        wrapper.eq(StringUtil.notEmpty(param.getAuthStatus()), "auth_status", param.getAuthStatus());
        //如果咨询时间-起始时间不为空并且不等于0，以咨询时间-起始时间为条件查询
        wrapper.ge(param.getStartTime() != null && param.getStartTime() != 0, "create_time", param.getStartTime());
        //如果咨询时间-结束时间不为空并且不等于0，以咨询时间-结束时间为条件查询
        wrapper.le(param.getEndTime() != null && param.getEndTime() != 0, "create_time", param.getEndTime());
        //如果匿名状态不为空，以匿名状态为条件查询
        wrapper.eq(StringUtil.notEmpty(param.getAnonymous()), "anonymous", param.getAnonymous());
        //按添加时间倒序查询
        wrapper.orderByDesc("create_time");
        //获取会员问题咨询分页列表数据
        IPage<MemberAsk> iPage = memberAskMapper.selectPage(new Page<>(param.getPageNo(), param.getPageSize()), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberAsk add(String askContent, Long goodsId, String anonymous) {
        //校验咨询内容长度
        if (askContent.length() < 3 || askContent.length() > 120) {
            throw new ServiceException(MemberErrorCode.E202.code(), "咨询内容应在3到120个字符之间");
        }

        //获取咨询的商品信息
        CacheGoods goods = goodsClient.getFromCache(goodsId);

        //获取当前登录的会员信息并校验是否为空
        Buyer buyer = UserContext.getBuyer();
        if (buyer == null) {
            throw new ServiceException(MemberErrorCode.E110.code(), "当前会员已经退出登录");
        }

        //在数据库中取出最新的会员信息
        Member member = memberManager.getModel(buyer.getUid());

        //新建会员问题咨询对象
        MemberAsk memberAsk = new MemberAsk();
        //设置咨询内容
        memberAsk.setContent(askContent);
        //设置会员ID
        memberAsk.setMemberId(member.getMemberId());
        //设置商品ID
        memberAsk.setGoodsId(goodsId);
        //设置商品名称
        memberAsk.setGoodsName(goods.getGoodsName());
        //设置商品图片
        memberAsk.setGoodsImg(goods.getThumbnail());
        //设置添加时间
        memberAsk.setCreateTime(DateUtil.getDateline());
        //咨询状态默认为正常状态
        memberAsk.setStatus(DeleteStatusEnum.NORMAL.value());
        //设置店铺ID
        memberAsk.setSellerId(goods.getSellerId());
        //商家回复状态默认为未回复
        memberAsk.setReplyStatus(CommonStatusEnum.NO.value());

        //校验是否匿名参数值是否正确
        if (!CommonStatusEnum.YES.value().equals(anonymous) && !CommonStatusEnum.NO.value().equals(anonymous)) {
            throw new ServiceException(MemberErrorCode.E107.code(), "是否匿名参数值不正确");
        }
        //设置是否匿名咨询
        memberAsk.setAnonymous(anonymous);
        //设置会员名称
        memberAsk.setMemberName(CommonStatusEnum.YES.value().equals(anonymous) ? "匿名" : member.getUname());
        //设置会员头像
        memberAsk.setMemberFace(member.getFace());

        //获取商品咨询设置
        String json = this.settingClient.get(SettingGroup.GOODS);
        //将json转为实体对象
        GoodsSettingVO goodsSettingVO = JsonUtil.jsonToObject(json, GoodsSettingVO.class);
        //设置咨询内容审核状态
        memberAsk.setAuthStatus(goodsSettingVO.getAskAuth().intValue() == 0 ? AuditEnum.PASS_AUDIT.name() : AuditEnum.WAIT_AUDIT.name());

        //会员商品咨询信息入库
        memberAskMapper.insert(memberAsk);

        //如果平台没有开启会员咨询审核，那么就直接发送消息
        if (goodsSettingVO.getAskAuth().intValue() == 0) {
            List<MemberAsk> list = new ArrayList<>();
            list.add(memberAsk);
            this.sendMessage(list);
        }

        return memberAsk;
    }


    @Override
    public MemberAsk getModel(Long askId) {
        return memberAskMapper.selectById(askId);
    }

    @Override
    public MemberAskVO getModelVO(Long askId) {
        //根据ID获取会员问题咨询内容
        MemberAsk memberAsk = this.getModel(askId);
        //非空校验
        if (memberAsk == null) {
            throw new ServiceException(MemberErrorCode.E202.code(), "会员商品咨询信息不存在");
        }

        //新建会员问题咨询实体对象VO
        MemberAskVO memberAskVO = new MemberAskVO();
        //复制实体对象值
        BeanUtil.copyProperties(memberAsk, memberAskVO);

        //获取商品信息并设置商品信息
        List<GoodsDO> goodsList = goodsClient.queryDo(new Long[]{memberAskVO.getGoodsId()});
        GoodsDO goods = goodsList.get(0);
        memberAskVO.setGoodsPrice(goods.getPrice());
        memberAskVO.setCommentNum(goods.getCommentNum());
        memberAskVO.setPraiseRate(goods.getGrade());

        return memberAskVO;
    }

    @Override
    public WebPage listGoodsAsks(Long pageNo, Long pageSize, Long goodsId) {
        //新建查询条件包装器
        QueryWrapper<MemberAskVO> wrapper = new QueryWrapper<>();
        //以问题咨询状态为条件查询
        wrapper.eq("status", DeleteStatusEnum.NORMAL.value());
        //以商品ID为条件查询
        wrapper.eq("goods_id", goodsId);
        //以问题咨询回复数量为条件查询
        wrapper.gt("reply_num", 0);
        //以创建时间和回复数量倒序排序
        wrapper.orderByDesc("create_time", "reply_num");
        //获取与会员商品咨询相关的其它咨询分页列表信息
        IPage<MemberAskVO> iPage = memberAskMapper.listGoodsAsks(new Page<>(pageNo, pageSize), wrapper);

        //类型转换
        WebPage webPage = PageConvert.convert(iPage);
        //获取结果集合
        List<MemberAskVO> memberAskVOS = webPage.getData();
        //循环结果集，设置问题咨询的首条回复内容
        for (MemberAskVO memberAskVO : memberAskVOS) {
            memberAskVO.setFirstReply(this.askReplyManager.getNewestModel(memberAskVO.getAskId()));
        }

        return webPage;
    }

    @Override
    public List<MemberAsk> listRelationAsks(Long askId, Long goodsId) {
        //新建查询条件包装器，
        QueryWrapper<MemberAsk> wrapper = new QueryWrapper<>();
        //以审核状态通过为查询条件
        wrapper.eq("auth_status", AuditEnum.PASS_AUDIT.value());
        //以问题咨询ID为查询条件
        wrapper.ne("ask_id", askId);
        //以商品ID为查询条件
        wrapper.eq("goods_id", goodsId);
        //以问题咨询回复数量大于0为查询条件
        wrapper.gt("reply_num", 0);
        //以创建时间在三个月内为查询条件
        wrapper.gt("create_time", DateUtil.getBeforeMonthDateline(3));
        //以回复数量、创建时间倒序排序
        wrapper.orderByDesc("reply_num", "create_time");
        //获取三个月之内有效的某个商品相关的问题咨询
        IPage<MemberAsk> iPage = memberAskMapper.selectPage(new Page<>(1, 10), wrapper);
        return iPage.getRecords();
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void batchAudit(BatchAuditVO batchAuditVO) {
        //校验是否有管理端权限
        Admin admin = AdminUserContext.getAdmin();
        //非空校验
        if (admin == null) {
            throw new NoPermissionException("没有权限审核会员咨询信息!");
        }
        //校验是否选择了要审核的商品咨询
        if (batchAuditVO.getIds() == null || batchAuditVO.getIds().length == 0) {
            throw new ServiceException(MemberErrorCode.E107.code(), "请选择要进行审核的会员商品咨询");
        }
        //校验审核参数值是否正确
        if (!AuditEnum.PASS_AUDIT.value().equals(batchAuditVO.getAuthStatus()) && !AuditEnum.REFUSE_AUDIT.value().equals(batchAuditVO.getAuthStatus())) {
            throw new ServiceException(MemberErrorCode.E107.code(), "审核状态参数值不正确");
        }

        //新建查询条件包装器
        QueryWrapper<MemberAsk> wrapper = new QueryWrapper<>();
        //以会员问题咨询ID为查询条件
        wrapper.in("ask_id", batchAuditVO.getIds());
        //获取会员问题咨询集合
        List<MemberAsk> memberAskList = memberAskMapper.selectList(wrapper);

        //循环结果集并修改审核状态
        for (MemberAsk memberAsk : memberAskList) {
            if (!AuditEnum.WAIT_AUDIT.value().equals(memberAsk.getAuthStatus())) {
                throw new ServiceException(MemberErrorCode.E107.code(), "内容为【" + memberAsk.getContent() + "】的咨询不是可以进行审核的状态");
            }

            //新建问题咨询对象
            MemberAsk ask = new MemberAsk();
            //设置审核状态
            ask.setAuthStatus(batchAuditVO.getAuthStatus());
            //设置问题咨询ID
            ask.setAskId(memberAsk.getAskId());
            //修改问题咨询信息
            memberAskMapper.updateById(ask);
        }

        //发送消息
        this.sendMessage(memberAskList);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long askId) {
        //将状态变成已删除状态
        MemberAsk ask = new MemberAsk();
        ask.setAskId(askId);
        ask.setStatus(DeleteStatusEnum.DELETED.value());
        memberAskMapper.updateById(ask);

        //同时删除咨询问题的回复和发送的站内消息
        this.askReplyManager.deleteByAskId(askId);
        this.askMessageManager.deleteByAskId(askId);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public MemberAsk reply(String replyContent, Long askId) {
        //校验回复内容
        if (StringUtil.isEmpty(replyContent)) {
            throw new ServiceException(MemberErrorCode.E202.code(), "回复内容不能为空");
        }
        //校验回复内容长度
        if (replyContent.length() < 3 || replyContent.length() > 120) {
            throw new ServiceException(MemberErrorCode.E202.code(), "回复内容应在3到120个字符之间");
        }
        //获取当前登录的商家信息
        Seller seller = UserContext.getSeller();
        //根据问题咨询ID获取问题咨询信息
        MemberAsk ask = this.getModel(askId);
        //权限校验
        if (ask == null || !seller.getSellerId().equals(ask.getSellerId())) {
            throw new ServiceException(MemberErrorCode.E200.code(), "无权回复");
        }
        //校验商家是否已经回复
        if (CommonStatusEnum.YES.value().equals(ask.getReplyStatus())) {
            throw new ServiceException(MemberErrorCode.E202.code(), "不可重复回复");
        }

        //设置回复内容
        ask.setReply(SensitiveFilter.filter(replyContent, CharacterConstant.WILDCARD_STAR));
        //设置回复状态
        ask.setReplyStatus(CommonStatusEnum.YES.value());
        //设置回复时间
        ask.setReplyTime(DateUtil.getDateline());
        //设置回复数量
        ask.setReplyNum(ask.getReplyNum() + 1);
        memberAskMapper.updateById(ask);
        return ask;
    }

    @Override
    public void updateReplyNum(Long askId, Integer num) {
        //修改会员商品咨询回复数量
        memberAskMapper.updateReplyNum(num, askId);
    }

    @Override
    public Integer getNoReplyCount(Long sellerId) {
        //新建查询条件包装器
        QueryWrapper<MemberAsk> wrapper = new QueryWrapper<>();
        //以问题咨询状态为正常作为查询条件
        wrapper.eq("status", DeleteStatusEnum.NORMAL.value());
        //以回复状态为未回复作为查询条件
        wrapper.eq("reply_status", CommonStatusEnum.NO.value());
        //以商家id作为查询条件
        wrapper.eq("seller_id", sellerId);
        //以审核状态为通过作为查询条件
        wrapper.eq("auth_status", AuditEnum.PASS_AUDIT.name());

        return memberAskMapper.selectCount(wrapper);
    }

    /**
     * 消息发送
     *
     * @param memberAskList
     */
    private void sendMessage(List<MemberAsk> memberAskList) {
        MemberAskMessage memberAskMessage = new MemberAskMessage();
        memberAskMessage.setMemberAsks(memberAskList);
        memberAskMessage.setSendTime(DateUtil.getDateline());
        this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_GOODS_ASK, AmqpExchange.MEMBER_GOODS_ASK + "_ROUTING", memberAskMessage));
    }
}
