package com.enation.app.javashop.api.seller.passport;

import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.CaptchaClient;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.dto.MemberDTO;
import com.enation.app.javashop.model.member.vo.MemberVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.exception.ServiceException;
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
import javax.validation.constraints.NotEmpty;


/**
 * 会员登录注册API
 *
 * @author zh
 * @version v7.0
 * @since v7.0
 * 2018年3月23日 上午10:12:12
 */
@RestController
@RequestMapping("/seller/register")
@Api(description = "商家注册API")
@Validated
public class PassportRegisterSellerController {

    @Autowired
    private PassportManager passportManager;
    @Autowired
    private CaptchaClient captchaClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private SmsClient smsClient;
    @Autowired
    private JavashopConfig javashopConfig;

    @PostMapping(value = "/smscode/{mobile}")
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid客户端的唯一标识", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "captcha", value = "图片验证码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "path"),
    })
    public String smsCode(@NotEmpty(message = "uuid不能为空") String uuid, @NotEmpty(message = "图片验证码不能为空") String captcha, @PathVariable("mobile") String mobile) {
        boolean isPass = captchaClient.valid(uuid, captcha, SceneType.REGISTER.name());
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "图片验证码不正确");
        }
        passportManager.sendRegisterSmsCode(mobile);
        //清除缓存图片验证码信息
        captchaClient.deleteCode(uuid, captcha, SceneType.REGISTER.name());
        return javashopConfig.getSmscodeTimout() / 60 + "";
    }

    @PostMapping("/pc")
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
        return memberManager.login(member.getUname(), memberDTO.getPassword(),2);

    }

}
