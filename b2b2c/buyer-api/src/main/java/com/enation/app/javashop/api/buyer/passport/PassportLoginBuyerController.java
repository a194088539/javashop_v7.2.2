package com.enation.app.javashop.api.buyer.passport;

import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.client.system.ValidatorClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.vo.MemberVO;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.exception.ServiceException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
@RequestMapping("/passport")
@Api(description = "会员登录API")
@Validated
public class PassportLoginBuyerController {

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

    @PostMapping(value = "/login/smscode/{mobile}")
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "path")
    })
    public String sendSmsCode(@PathVariable("mobile") String mobile) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        passportManager.sendLoginSmsCode(mobile);
        return javashopConfig.getSmscodeTimout() / 60 + "";
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户名（手机号）/密码登录API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    public MemberVO login(@NotEmpty(message = "用户名不能为空") String username, @NotEmpty(message = "密码不能为空") String password) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        //校验账号信息是否正确
        return memberManager.login(username, password,1);
    }

    @PostMapping("/login/{mobile}")
    @ApiOperation(value = "手机号码登录API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "sms_code", value = "手机验证码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "uuid", value = "客户端唯一标识", required = true, dataType = "String", paramType = "query")
    })
    public MemberVO mobileLogin(@PathVariable String mobile, @ApiIgnore @NotEmpty(message = "短信验证码不能为空") String smsCode) {
        boolean isPass = smsClient.valid(SceneType.LOGIN.name(), mobile, smsCode);
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码错误！");
        }
        return memberManager.mobileLogin(mobile,1);

    }
}
