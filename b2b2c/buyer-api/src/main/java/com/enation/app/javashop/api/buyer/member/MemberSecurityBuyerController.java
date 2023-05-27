package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.EmailClient;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.client.system.ValidatorClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberSecurityManager;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * 会员安全控制器
 *
 * @author zh
 * @version v7.0
 * @date 18/4/23 下午3:30
 * @since v7.0
 */
@RestController
@RequestMapping("/members")
@Validated
@Api(description = "会员安全API")
public class MemberSecurityBuyerController {

    @Autowired
    private MemberSecurityManager memberSecurityManager;
    @Autowired
    private ValidatorClient validatorClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private SmsClient smsClient;
    @Autowired
    private Cache cache;
    @Autowired
    private JavashopConfig javashopConfig;
    @Autowired
    private EmailClient emailClient;

    @PostMapping(value = "/security/send")
    @ApiOperation(value = "发送手机验证验证码")
    public String sendValSmsCode() {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        Buyer buyer = UserContext.getBuyer();
        Member member = memberManager.getModel(buyer.getUid());
        if (member == null || StringUtil.isEmpty(member.getMobile())) {
            throw new ServiceException(MemberErrorCode.E114.code(), "当前会员未绑定手机号");
        }

        memberSecurityManager.sendValidateSmsCode(member.getMobile());
        //将验证码失效时间返回，用于前端提示
        return javashopConfig.getSmscodeTimout() / 60 + "";
    }


    @PostMapping(value = "/security/bind/send/{mobile}")
    @ApiOperation(value = "发送绑定手机验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "path")
    })
    public String sendBindSmsCode(@PathVariable("mobile") String mobile) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        //发送绑定手机号码
        memberSecurityManager.sendBindSmsCode(mobile);
        return null;
    }

    @PutMapping("/security/bind/{mobile}")
    @ApiOperation(value = "手机号码绑定API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "sms_code", value = "手机验证码", required = true, dataType = "String", paramType = "query"),
    })
    public String bindMobile(@PathVariable String mobile, @ApiIgnore @NotEmpty(message = "短信验证码不能为空") String smsCode) {
        boolean isPass = smsClient.valid(SceneType.BIND_MOBILE.name(), mobile, smsCode);
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码错误");
        }
        memberSecurityManager.bindMobile(mobile);
        return null;
    }

    @GetMapping(value = "/security/exchange-bind")
    @ApiOperation(value = "验证换绑验证验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sms_code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    public String checkExchangeBindCode(@Valid @ApiIgnore @NotEmpty(message = "验证码不能为空") String smsCode) {
        return this.valSmsCode(smsCode);

    }

    /**
     * 验证手机验证码
     *
     * @param code 验证码
     * @return
     */
    private String valSmsCode(String code) {
        Buyer buyer = UserContext.getBuyer();
        Member member = memberManager.getModel(buyer.getUid());
        if (member == null || StringUtil.isEmpty(member.getMobile())) {
            throw new ServiceException(MemberErrorCode.E114.code(), "当前会员未绑定手机号");
        }
        boolean isPass = smsClient.valid(SceneType.VALIDATE_MOBILE.name(), member.getMobile(), code);
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码不正确");
        }
        return null;
    }


    @PutMapping("/security/exchange-bind/{mobile}")
    @ApiOperation(value = "手机号码换绑API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "sms_code", value = "手机验证码", required = true, dataType = "String", paramType = "query"),
    })
    public String exchangeBindMobile(@PathVariable String mobile, @ApiIgnore @NotEmpty(message = "短信验证码不能为空") String smsCode) {
        boolean isPass = smsClient.valid(SceneType.BIND_MOBILE.name(), mobile, smsCode);
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码错误");
        }
        memberSecurityManager.changeBindMobile(mobile);
        return null;
    }

    @GetMapping(value = "/security/password")
    @ApiOperation(value = "验证修改密码验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sms_code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    public String checkUpdatePwdCode(@Valid @ApiIgnore @NotEmpty(message = "验证码不能为空") String smsCode) {
        return this.valSmsCode(smsCode);

    }


    @PutMapping(value = "/security/password")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    public String updatePassword(@NotEmpty(message = "密码不能为空") String password) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        Buyer buyer = UserContext.getBuyer();
        Member member = memberManager.getModel(buyer.getUid());
        String str = StringUtil.toString(cache.get(CachePrefix.MOBILE_VALIDATE.getPrefix() + "_" + SceneType.VALIDATE_MOBILE.name() + "_" + member.getMobile()));
        if (StringUtil.isEmpty(str)) {
            throw new ServiceException(MemberErrorCode.E115.code(), "请先对当前用户进行身份校验");
        }
        memberSecurityManager.updatePassword(buyer.getUid(), password);
        return null;
    }

    @PostMapping(value = "/security/bind/email/send")
    @ApiOperation(value = "发送绑定邮箱验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "电子邮箱", required = true, dataType = "String", paramType = "query")
    })
    public String sendBindEmailCode(@NotEmpty(message = "电子邮箱不能为空") String email) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        //发送绑定邮箱验证码
        memberSecurityManager.sendBindEmailCode(email);
        return null;
    }

    @PostMapping("/security/bind/email")
    @ApiOperation(value = "电子邮箱绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "电子邮箱", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "email_code", value = "电子邮箱验证码", required = true, dataType = "String", paramType = "query")
    })
    public String bindEmail(@ApiIgnore @NotEmpty(message = "电子邮箱不能为空") String email, @ApiIgnore @NotEmpty(message = "电子邮箱验证码不能为空") String emailCode) {
        boolean isPass = emailClient.valid(SceneType.BIND_EMAIL.name(), email, emailCode);
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "邮箱验证码错误");
        }
        memberSecurityManager.bindEmail(email);
        return null;
    }
}
