package com.enation.app.javashop.service.member.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.mapper.member.MemberMapper;
import com.enation.app.javashop.model.base.message.MemberRegisterMsg;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.framework.redis.RedisChannel;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.service.member.MemberCollectionGoodsManager;
import com.enation.app.javashop.service.member.MemberCollectionShopManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.MemberQueryParam;
import com.enation.app.javashop.model.member.dto.MemberStatisticsDTO;
import com.enation.app.javashop.model.member.vo.BackendMemberVO;
import com.enation.app.javashop.model.member.vo.MemberLoginMsg;
import com.enation.app.javashop.model.member.vo.MemberPointVO;
import com.enation.app.javashop.model.member.vo.MemberVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.trade.order.enums.CommentStatusEnum;
import com.enation.app.javashop.framework.auth.Token;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.TokenManager;
import com.enation.app.javashop.framework.security.message.UserDisableMsg;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.security.model.Role;
import com.enation.app.javashop.framework.util.*;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 会员业务类
 *
 * @author zh
 * @version v2.0
 * @since v7.0.0
 * 2018-03-16 11:33:56
 */
@Service
public class MemberManagerImpl implements MemberManager {

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private MemberCollectionShopManager memberCollectionShopManager;
    @Autowired
    private MemberCollectionGoodsManager memberCollectionGoodsManager;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public WebPage list(MemberQueryParam memberQueryParam) {
        //新建查询条件包装器
        QueryWrapper<Member> wrapper = new QueryWrapper<>();

        //如果会员状态不为空
        if (memberQueryParam.getDisabled() != null) {
            //如果会员状态不等于正常或者删除状态，则默认以正常状态进行查询 0：正常，-1：删除
            if (memberQueryParam.getDisabled() != -1 && memberQueryParam.getDisabled() != 0) {
                wrapper.eq("disabled", 0);
            } else {
                //以会员状态为条件进行查询
                wrapper.eq("disabled", memberQueryParam.getDisabled());
            }
        } else {
            //以会员状态为正常状态进行查询
            wrapper.eq("disabled", 0);
        }
        //如果查询关键字不为空，则以会员用户名或者手机号或者会员昵称为条件进行模糊查询
        wrapper.and(StringUtil.notEmpty(memberQueryParam.getKeyword()), ew -> {
            ew.like("uname", memberQueryParam.getKeyword())
                    .or().like("mobile", memberQueryParam.getKeyword())
                    .or().like("nickname", memberQueryParam.getKeyword());
        });

        //如果会员手机号码不为空，则以会员手机号码为条件模糊查询
        wrapper.like(StringUtil.notEmpty(memberQueryParam.getMobile()), "mobile", memberQueryParam.getMobile());
        //如果用户名不为空，则以用户名为条件模糊查询
        wrapper.like(StringUtil.notEmpty(memberQueryParam.getUname()), "uname", memberQueryParam.getUname());
        //如果会员邮箱不为空，则以会员邮箱为条件查询
        wrapper.eq(StringUtil.notEmpty(memberQueryParam.getEmail()), "email", memberQueryParam.getEmail());
        //如果会员性别不为空并且值等于1或2，则以会员性别为条件查询 1：男，0：女
        wrapper.eq(memberQueryParam.getSex() != null && (memberQueryParam.getSex() == 1 || memberQueryParam.getSex() == 0), "sex", memberQueryParam.getSex());
        //如果会员注册时间-时间范围起始时间不为空，则查询注册时间大于这个时间的会员信息
        wrapper.gt(StringUtil.notEmpty(memberQueryParam.getStartTime()), "create_time", memberQueryParam.getStartTime());
        //如果会员注册时间-时间范围结束时间不为空，则查询注册时间小于这个时间的会员信息
        wrapper.lt(StringUtil.notEmpty(memberQueryParam.getEndTime()), "create_time", memberQueryParam.getEndTime());

        //如果地区信息不为空，则以地区信息作为条件查询
        if (memberQueryParam.getRegion() != null) {
            wrapper.eq("province_id", memberQueryParam.getRegion().getProvinceId())
                    .eq("city_id", memberQueryParam.getRegion().getCityId())
                    .eq("county_id", memberQueryParam.getRegion().getCountyId());
            //如果地区中的乡镇id不等于0，则以乡镇ID作为条件查询
            wrapper.eq(!memberQueryParam.getRegion().getTownId().equals(0), "town_id", memberQueryParam.getRegion().getTownId());
        }
        //以会员注册时间倒序排序
        wrapper.orderByDesc("create_time");
        //获取会员分页列表数据
        IPage<Member> iPage = memberMapper.selectPage(new Page<>(memberQueryParam.getPageNo(), memberQueryParam.getPageSize()), wrapper);
        return PageConvert.convert(iPage);
    }

    /**
     * 修改会员登录次数
     *
     * @param memberId
     * @param now
     */
    @Override
    public void updateLoginNum(Long memberId, Long now) {
        //新建修改条件包装器
        UpdateWrapper<Member> wrapper = new UpdateWrapper<>();
        //修改会员最后登录时间以及登录次数+1，并以会员ID为修改条件
        wrapper.set("last_login", now).setSql("login_count = login_count+1").eq("member_id", memberId);
        //修改会员信息
        memberMapper.update(new Member(), wrapper);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Member edit(Member member, Long id) {
        //校验邮箱是否已经存在
        if (!StringUtil.isEmpty(member.getEmail())) {
            Member mb = this.getMemberByEmail(member.getEmail());
            if (mb != null && !mb.getMemberId().equals(id)) {
                throw new ServiceException(MemberErrorCode.E117.code(), "邮箱已经被占用");
            }
        }
        //校验用户名是否已经存在
        if (!StringUtil.isEmpty(member.getUname())) {
            Member mb = this.getMemberByName(member.getUname());
            if (mb != null && !mb.getMemberId().equals(id)) {
                throw new ServiceException(MemberErrorCode.E108.code(), "当前用户名已经被使用");
            }
        }

        //校验手机号码是否重复
        if (!StringUtil.isEmpty(member.getMobile())) {
            Member mb = this.getMemberByMobile(member.getMobile());
            if (mb != null && !mb.getMemberId().equals(id)) {
                throw new ServiceException(MemberErrorCode.E118.code(), "当前手机号已经被使用");
            }
        }
        //设置会员ID
        member.setMemberId(id);
        //修改会员信息
        memberMapper.updateById(member);
        return member;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void disable(Long id) {
        //新建会员对象
        Member member = new Member();
        //设置会员状态为已删除
        member.setDisabled(-1);
        //设置会员ID
        member.setMemberId(id);
        //通过会员ID修改会员信息
        memberMapper.updateById(member);

        //发送redis订阅消息,通知各个节点此用户已经禁用
        UserDisableMsg userDisableMsg = new UserDisableMsg(id, Role.BUYER, UserDisableMsg.ADD);
        String msgJson = JsonUtil.objectToJson(userDisableMsg);
        redisTemplate.convertAndSend(RedisChannel.USER_DISABLE, msgJson);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void recoverySendMsg(Long id) {
        //发送redis订阅消息,通知各个节点此用户已经解禁
        UserDisableMsg userDisableMsg = new UserDisableMsg(id, Role.BUYER, UserDisableMsg.DELETE);
        String msgJson = JsonUtil.objectToJson(userDisableMsg);
        redisTemplate.convertAndSend(RedisChannel.USER_DISABLE, msgJson);
    }

    @Override
    public void delete(Class<Member> clazz, Long id) {
        memberMapper.deleteById(id);
    }

    @Override
    public Member getModel(Long id) {
        return memberMapper.selectById(id);
    }

    @Override
    public MemberPointVO getMemberPoint() {
        //获取当前登录的会员信息
        Buyer buyer = UserContext.getBuyer();
        //通过当前登录的会员信息ID获取会员完整信息
        Member member = this.getModel(buyer.getUid());
        //非空校验
        if (member != null) {
            //新建会员积分对象VO
            MemberPointVO memberPointVO = new MemberPointVO();
            //如果会员消费积分不为空，则填充消费积分；如果为空，默认填充0积分
            if (member.getConsumPoint() != null) {
                memberPointVO.setConsumPoint(member.getConsumPoint());
            } else {
                memberPointVO.setConsumPoint(0L);
            }

            //如果会员等级积分不为空，则填充等级积分；如果为空，默认填充0积分
            if (member.getGradePoint() != null) {
                memberPointVO.setGradePoint(member.getGradePoint());
            } else {
                memberPointVO.setGradePoint(0L);
            }
            return memberPointVO;
        }
        throw new ResourceNotFoundException("此会员不存在！");

    }

    @Override
    public Member getMemberByName(String uname) {
        return memberMapper.getMemberByUname(uname);
    }

    @Override
    public Member getMemberByMobile(String mobile) {
        //新建查询条件包装器
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        //以会员手机号为查询条件
        wrapper.eq("mobile", mobile);
        return this.getMember(wrapper);
    }

    @Override
    public String[] generateMemberUname(String uname) {
        //如果用户输入的用户大于15位 则截取 拼接随机数5位，总长度不能大于二十
        if (uname.length() > 15) {
            uname = uname.substring(0, 15);

        }
        String[] strs = new String[2];
        int i = 0;
        while (true) {
            if (i > 1) {
                break;
            }
            String unameRandom = "" + (int) (Math.random() * (99999 - 10000 + 1));
            //根据拼接好的用户判断是否存在
            Member member = this.getMemberByName(uname + unameRandom);
            if (member == null) {
                strs[i] = uname + unameRandom;
                i++;
            }
        }
        return strs;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Member register(Member member) {
        //手机号码校验
        Member m = this.getMemberByMobile(member.getMobile());
        if (m != null) {
            throw new ServiceException(MemberErrorCode.E107.code(), "该手机号已经被占用");
        }
        //用户名校验
        m = this.getMemberByName(member.getUname());
        if (m != null) {
            throw new ServiceException(MemberErrorCode.E107.code(), "当前会员已经注册");
        }
        //邮箱校验
        if (!StringUtil.isEmpty(member.getEmail())) {
            m = this.getMemberByEmail(member.getEmail());
            if (m != null) {
                throw new ServiceException(MemberErrorCode.E117.code(), "邮箱已经被占用");
            }
        }
        //获取前端传入的密码
        String password = member.getPassword();
        //将密码+用户名进行MD5加密，然后放入会员对象中
        member.setPassword(StringUtil.md5(password + member.getUname().toLowerCase()));
        //设置会员注册时间为当前时间
        member.setCreateTime(DateUtil.getDateline());
        //设置会员等级积分为0
        member.setGradePoint(0L);
        //设置会员消费积分为0
        member.setConsumPoint(0L);
        //设置会员登录次数为0
        member.setLoginCount(0);
        //设置会员是否开启店铺 0：否，1：是
        member.setHaveShop(0);
        //设置会员是否被禁用 0：否，1：是
        member.setDisabled(0);
        //设置会员是否完善了个人信息 0：否，1：是
        member.setInfoFull(0);
        //会员信息入库
        memberMapper.insert(member);

        //组织数据结构发送会员注册消息
        MemberRegisterMsg memberRegisterMsg = new MemberRegisterMsg();
        memberRegisterMsg.setMember(member);
        memberRegisterMsg.setUuid(ThreadContextHolder.getHttpRequest().getHeader("uuid"));
        this.messageSender.send(new MqMessage(AmqpExchange.MEMEBER_REGISTER, AmqpExchange.MEMEBER_REGISTER + "_ROUTING", memberRegisterMsg));
        return member;
    }


    @Override
    public MemberVO login(String username, String password, Integer memberOrSeller) {
        //校验用户名密码
        Member member = this.validation(username, password);
        return this.loginHandle(member, memberOrSeller);
    }


    @Override
    public MemberVO mobileLogin(String phone, Integer memberOrSeller) {
        //根据手机号获取会员信息
        Member member = this.getMemberByMobile(phone);
        if (member != null) {
            if (member.getDisabled().equals(-1)) {
                throw new ServiceException(MemberErrorCode.E107.code(), "当前账号已经禁用，请联系管理员");
            }
        } else {
            throw new ServiceException(MemberErrorCode.E107.code(), "账号密码错误！");
        }
        //登录后处理
        return this.loginHandle(member, memberOrSeller);
    }

    /**
     * 登录会员后的处理
     *
     * @param member         会员信息
     * @param memberOrSeller 会员还是卖家，1 会员  2 卖家
     */
    @Override
    public MemberVO loginHandle(Member member, Integer memberOrSeller) {
        //从请求header中获取用户的uuid
        String uuid = ThreadContextHolder.getHttpRequest().getHeader("uuid");
        //初始化会员信息
        MemberVO memberVO = this.convertMember(member, uuid);
        //发送登录消息
        this.sendMessage(member, memberOrSeller);

        return memberVO;
    }

    @Override
    public MemberVO connectLoginHandle(Member member, String uuid) {
        //初始化会员信息
        MemberVO memberVO = this.convertMember(member, uuid);
        //发送登录消息
        this.sendMessage(member, 1);
        return memberVO;
    }

    @Override
    public Member validation(String username, String password) {
        String pwdmd5 = "";
        //用户名登录处理
        Member member = this.getMemberByName(username);
        if (member != null) {
            if (!StringUtil.equals(member.getUname(), username)) {
                throw new ServiceException(MemberErrorCode.E107.code(), "账号密码错误！");
            }
            pwdmd5 = StringUtil.md5(password + member.getUname().toLowerCase());
            if (member.getPassword().equals(pwdmd5)) {
                return member;
            }
        }
        //手机号码登录处理
        member = this.getMemberByMobile(username);
        if (member != null) {
            pwdmd5 = StringUtil.md5(password + member.getUname().toLowerCase());
            if (member.getPassword().equals(pwdmd5)) {
                return member;
            }
        }
        //邮箱登录处理
        member = this.getMemberByEmail(username);
        if (member != null) {
            pwdmd5 = StringUtil.md5(password + member.getUname().toLowerCase());
            if (member.getPassword().equals(pwdmd5)) {
                return member;
            }
        }
        throw new ServiceException(MemberErrorCode.E107.code(), "账号密码错误！");
    }

    @Override
    public Member getMemberByAccount(String account) {
        //通过手机号进行查询账户信息
        Member member = this.getMemberByMobile(account);
        if (member != null) {
            return member;
        }
        //通过用户名进行查询账户信息
        member = this.getMemberByName(account);
        if (member != null) {
            return member;
        }
        //通过邮箱进行查询账户信息
        member = this.getMemberByEmail(account);
        if (member != null) {
            return member;
        }
        throw new ResourceNotFoundException("此会员不存在");
    }

    @Override
    public void logout() {

    }

    @Override
    public Member getMemberByEmail(String email) {
        //新建查询条件包装器
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        //以会员邮箱作为条件进行查询
        wrapper.eq("email", email);
        return this.getMember(wrapper);
    }


    @Override
    public MemberStatisticsDTO getMemberStatistics() {
        MemberStatisticsDTO memberStatisticsDTO = new MemberStatisticsDTO();
        //会员收藏店铺数
        memberStatisticsDTO.setShopCollectCount(memberCollectionShopManager.getMemberCollectCount());
        //会员收藏商品数
        memberStatisticsDTO.setGoodsCollectCount(memberCollectionGoodsManager.getMemberCollectCount());
        //会员订单数
        memberStatisticsDTO.setOrderCount(orderClient.getOrderNumByMemberID(UserContext.getBuyer().getUid()));
        //待评论数
        memberStatisticsDTO.setPendingCommentCount(orderClient.getOrderCommentNumByMemberID(UserContext.getBuyer().getUid(), CommentStatusEnum.UNFINISHED.name()));
        return memberStatisticsDTO;
    }

    @Override
    public List<BackendMemberVO> newMember(Integer length) {
        return memberMapper.selectNewMember(length);
    }

    @Override
    public List<Member> getMemberByIds(Long[] memberIds) {
        //新建查询条件包装器
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        //以会员ID为查询条件
        wrapper.in("member_id", memberIds);
        return memberMapper.selectList(wrapper);
    }

    @Override
    public void loginNumToZero() {
        //新建修改条件包装器
        UpdateWrapper<Member> wrapper = new UpdateWrapper<>();
        //设置登录次数为0
        wrapper.set("login_count", 0);
        //所有会员登录次数修改为0
        memberMapper.update(new Member(), wrapper);
    }

    @Override
    public void memberLoginout(Long memberId) {

    }

    @Override
    public List<String> queryAllMemberIds() {

        List<Member> list = this.memberMapper.selectList(new QueryWrapper<Member>());
        return list.stream().map(m -> m.getMemberId().toString()).collect(Collectors.toList());
    }

    @Autowired
    private TokenManager tokenManager;

    /**
     * 生成member的token
     *
     * @param member
     * @param uuid
     * @return
     */
    private MemberVO convertMember(Member member, String uuid) {
        //校验当前账号是否被禁用
        if (!member.getDisabled().equals(0)) {
            throw new ServiceException(MemberErrorCode.E107.code(), "当前账号已经禁用，请联系管理员");
        }

        //新建买家用户角色对象
        Buyer buyer = new Buyer();
        //设置用户ID
        buyer.setUid(member.getMemberId());
        //设置用户名称
        buyer.setUsername(member.getUname());
        //设置uuid
        buyer.setUuid(uuid);
        //创建Token
        Token token = tokenManager.create(buyer);
        //获取访问Token
        String accessToken = token.getAccessToken();
        //获取刷新Token
        String refreshToken = token.getRefreshToken();
        //组织返回数据
        MemberVO memberVO = new MemberVO(member, accessToken, refreshToken);
        return memberVO;
    }

    /**
     * 发送登录消息
     *
     * @param member
     * @param memberOrSeller
     */
    private void sendMessage(Member member, Integer memberOrSeller) {
        MemberLoginMsg loginMsg = new MemberLoginMsg();
        loginMsg.setLastLoginTime(member.getLastLogin());
        loginMsg.setMemberId(member.getMemberId());
        loginMsg.setMemberOrSeller(memberOrSeller);
        this.messageSender.send(new MqMessage(AmqpExchange.MEMEBER_LOGIN, AmqpExchange.MEMEBER_LOGIN + "_ROUTING", loginMsg));
    }

    /**
     * 获取会员信息
     *
     * @param wrapper 查询条件构造器
     * @return
     */
    private Member getMember(QueryWrapper<Member> wrapper) {
        return memberMapper.selectOne(wrapper);
    }
}
