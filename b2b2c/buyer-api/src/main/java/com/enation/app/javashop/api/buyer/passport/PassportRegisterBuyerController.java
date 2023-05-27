package com.enation.app.javashop.api.buyer.passport;

import com.enation.app.javashop.model.base.CharacterConstant;
import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.client.system.ValidatorClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.MemberDTO;
import com.enation.app.javashop.model.member.vo.MemberVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.model.util.sensitiveutil.SensitiveFilter;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.validation.annotation.Mobile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;


/**
 * 会员登录注册API
 *
 * @author zh
 * @version v7.0
 * @since v7.0
 * 2018年3月23日 上午10:12:12
 */
@RestController
@RequestMapping("/passport")
@Api(description = "会员注册API")
@Validated
public class PassportRegisterBuyerController {

    @Autowired
    private PassportManager passportManager;
    @Autowired
    private ValidatorClient validatorClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private SmsClient smsClient;
    @Autowired
    private JavashopConfig javashopConfig;

    @PostMapping(value = "/register/smscode/{mobile}")
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "path")
    })
    public String smsCode(@PathVariable("mobile") String mobile) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        passportManager.sendRegisterSmsCode(mobile);
        return javashopConfig.getSmscodeTimout() / 60 + "";
    }

    @PostMapping("/register/pc")
    @ApiOperation(value = "PC注册")
    public MemberVO registerForPC(@Valid MemberDTO memberDTO) {
        boolean bool = smsClient.valid(SceneType.REGISTER.name(), memberDTO.getMobile(), memberDTO.getSmsCode());
        if (!bool) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码错误");
        }
        //对用户名的校验处理
        String username = memberDTO.getUsername();
        if (username.contains("@")) {
            throw new ServiceException(MemberErrorCode.E107.code(), "用户名中不能包含@等特殊字符！");
        }
        //如果用户名包含敏感词不能注册
        String uname = SensitiveFilter.filter(username, CharacterConstant.WILDCARD_STAR);
        if(!username.equals(uname)){
            throw new ServiceException(MemberErrorCode.E107.code(), "用户名中不能包含敏感词汇");
        }

        Member member = new Member();
        member.setUname(memberDTO.getUsername());
        member.setPassword(memberDTO.getPassword());
        member.setNickname(memberDTO.getUsername());
        member.setMobile(memberDTO.getMobile());
        member.setSex(1);
        member.setRegisterIp(ThreadContextHolder.getHttpRequest().getRemoteAddr());
        //注册
        memberManager.register(member);
        //登录
        return memberManager.login(member.getUname(), memberDTO.getPassword(),1);

    }


    @PostMapping("/register/wap")
    @ApiOperation(value = "wap注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    public MemberVO registerForWap(@Mobile String mobile, @Pattern(regexp = "[a-fA-F0-9]{32}", message = "密码格式不正确") String password) {

        String validMobile = this.smsClient.validMobile(SceneType.REGISTER.name(),mobile);

        if(StringUtil.isEmpty(validMobile) || !mobile.equals(validMobile)){
            throw new ServiceException(MemberErrorCode.E115.code(), "请先对手机进行验证");
        }

        Member member = new Member();
        member.setMobile(mobile);
        member.setSex(1);
        member.setPassword(password);
        member.setUname("m_" + mobile);
        member.setNickname("m_" + mobile);
        member.setRegisterIp(ThreadContextHolder.getHttpRequest().getRemoteAddr());
        //注册
        memberManager.register(member);
        //获取token
        return memberManager.login(member.getUname(), password,1);
    }

}
