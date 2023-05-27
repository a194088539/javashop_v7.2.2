package com.enation.app.javashop.api.buyer.passport;

import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.client.system.ValidatorClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberSecurityManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.JsonUtil;
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
import java.util.HashMap;
import java.util.Map;

/**
 * 会员找回密码api
 *
 * @author zh
 * @version v7.0
 * @date 18/5/16 下午4:07
 * @since v7.0
 */
@RestController
@RequestMapping("/passport")
@Api(description = "会员找回密码api")
@Validated
public class PassportFindPasswordBuyerController {

    @Autowired
    private ValidatorClient validatorClient;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private Cache cache;
    @Autowired
    private PassportManager passportManager;
    @Autowired
    private MemberSecurityManager memberSecurityManager;
    @Autowired
    private SmsClient smsClient;
    @Autowired
    private JavashopConfig javashopConfig;


    @ApiOperation(value = "获取账户信息")
    @GetMapping("find-pwd")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid客户端的唯一标识", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "account", value = "账户名称", required = true, dataType = "String", paramType = "query")
    })
    public String getMemberInfo(@NotEmpty(message = "uuid不能为空") String uuid, @NotEmpty(message = "账户名称不能为空") String account) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        //对会员状态进行校验
        Member member = memberManager.getMemberByAccount(account);
        if (!member.getDisabled().equals(0)) {
            throw new ServiceException(MemberErrorCode.E107.code(), "当前账号已经禁用，请联系管理员");
        }
        //对获得的会员信息进行处理
        String mobile = member.getMobile();
        mobile = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        //对用户名的处理
        String name = member.getUname();
        //将数据组织好json格式返回
        uuid = StringUtil.getUUId();
        Map map = new HashMap(16);
        map.put("mobile", mobile);
        map.put("uname", name.substring(0, 1) + "***" + name.substring(name.length() - 1, name.length()));
        map.put("uuid", uuid);
        cache.put(uuid, member, javashopConfig.getSmscodeTimout());
        return JsonUtil.objectToJson(map);

    }


    @PostMapping(value = "/find-pwd/send")
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid客户端的唯一标识", required = true, dataType = "String", paramType = "query")
    })
    public String sendSmsCode(@NotEmpty(message = "uuid不能为空") String uuid) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        Member member = (Member) cache.get(uuid);
        if (member != null) {
            passportManager.sendFindPasswordCode(member.getMobile());
            return javashopConfig.getSmscodeTimout() / 60 + "";
        }
        throw new ServiceException(MemberErrorCode.E119.code(), "请先对当前用户进行身份校验");
    }

    @PutMapping(value = "/find-pwd/update-password")
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid客户端的唯一标识", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    public String updatePassword(@NotEmpty(message = "uuid不能为空") String uuid, String password) {
        Member member = (Member) cache.get(uuid);
        if (member != null) {
            memberSecurityManager.updatePassword(member.getMemberId(), password);
            return null;
        }
        cache.remove(uuid);
        throw new ServiceException(MemberErrorCode.E119.code(), "请先对当前用户进行身份校验");
    }

    @GetMapping(value = "/find-pwd/valid")
    @ApiOperation(value = "验证找回密码验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", value = "uuid客户端的唯一标识", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "sms_code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    public String updateCodeCheck(@Valid @ApiIgnore @NotEmpty(message = "验证码不能为空") String smsCode, @NotEmpty(message = "uuid不能为空") String uuid) {
        Member member = (Member) cache.get(uuid);
        if (member == null) {
            throw new ServiceException(MemberErrorCode.E119.code(), "请先对当前用户进行身份校验");
        }
        if (StringUtil.isEmpty(member.getMobile())) {
            cache.remove(uuid);
            throw new ServiceException(MemberErrorCode.E119.code(), "请先对账户进行手机号码绑定在进行此操作");
        }
        boolean isPass = smsClient.valid(SceneType.VALIDATE_MOBILE.name(), member.getMobile(), smsCode);
        if (!isPass) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码不正确");
        }
        return null;

    }


}
