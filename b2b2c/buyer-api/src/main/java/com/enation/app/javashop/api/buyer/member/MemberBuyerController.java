package com.enation.app.javashop.api.buyer.member;


import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.MemberEditDTO;
import com.enation.app.javashop.model.member.dto.MemberStatisticsDTO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import com.enation.app.javashop.framework.rabbitmq.MessageSender;
import com.enation.app.javashop.framework.rabbitmq.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * 会员控制器
 *
 * @author zh
 * @version v2.0
 * @since v7.0.0
 * 2018-03-16 11:33:56
 */
@RestController
@RequestMapping("/members")
@Api(description = "会员相关API")
public class MemberBuyerController {

    @Autowired
    private MemberManager memberManager;
    @Autowired
    private MessageSender messageSender;


    @PutMapping
    @ApiOperation(value = "完善会员细信息", response = Member.class)
    public Member perfectInfo(@Valid MemberEditDTO memberEditDTO) {
        Buyer buyer = UserContext.getBuyer();
        Member member = memberManager.getModel(buyer.getUid());
        //判断数据库是否存在此会员
        if (member == null) {
            throw new ResourceNotFoundException("此会员不存在");
        }
        //过滤用户昵称敏感词汇
        memberEditDTO.setNickname(SensitiveFilter.filter(memberEditDTO.getNickname(), CharacterConstant.WILDCARD_STAR));
        BeanUtil.copyProperties(memberEditDTO, member);
        if (memberEditDTO.getRegion() != null) {
            if (StringUtil.isEmpty(memberEditDTO.getRegion().getCounty())) {
                throw new ServiceException(MemberErrorCode.E141.code(), "地区不合法");
            }
            BeanUtil.copyProperties(memberEditDTO.getRegion(), member);
        }
        //判断会员是修改资料还是完善资料
        if (member.getInfoFull() != null && !member.getInfoFull().equals(1)) {
            member.setInfoFull(1);
            this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_INFO_COMPLETE, "member-info-complete-routingkey", member.getMemberId()));
        }
        member.setFace(memberEditDTO.getFace());
        member.setTel(memberEditDTO.getTel());
        this.memberManager.edit(member, buyer.getUid());
        //发送会员资料变化消息
        this.messageSender.send(new MqMessage(AmqpExchange.MEMBER_INFO_CHANGE, AmqpExchange.MEMBER_INFO_CHANGE + "_ROUTING", member.getMemberId()));
        return member;
    }


    @GetMapping
    @ApiOperation(value = "查询当前会员信息")
    public Member get() {
        return this.memberManager.getModel(UserContext.getBuyer().getUid());
    }

    @ApiOperation(value = "注销会员登录")
    @PostMapping(value = "/logout")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "会员id", dataType = "int", paramType = "query", required = true)
    })
    public String loginOut(@NotNull(message = "会员id不能为空") Long uid) {
        this.memberManager.logout();
        return "";
    }

    @GetMapping("/statistics")
    @ApiOperation(value = "统计当前会员的一些数据")
    public MemberStatisticsDTO getMemberStatistics() {
        return this.memberManager.getMemberStatistics();
    }

}
