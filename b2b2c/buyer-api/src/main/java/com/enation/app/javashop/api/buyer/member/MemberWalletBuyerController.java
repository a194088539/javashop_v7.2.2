package com.enation.app.javashop.api.buyer.member;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.client.system.ValidatorClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.model.member.vo.MemberDepositeVO;
import com.enation.app.javashop.service.member.DepositeManager;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.model.Buyer;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员钱包控制器
 * @author liuyulei
 * @version v1.0
 * @since v7.2.0
 * 2019-12-30 16:24:51
 */
@RestController
@RequestMapping("/members/wallet")
@Api(description = "会员预存款相关API")
public class MemberWalletBuyerController {

	@Autowired
	private DepositeManager depositeManager;

	@Autowired
	private ValidatorClient validatorClient;

	@Autowired
	private PassportManager passportManager;

	@Autowired
	private JavashopConfig javashopConfig;

	@Autowired
	private SmsClient smsClient;

	@Autowired
	private Cache cache;

	@Autowired
	private MemberManager memberManager;


	@GetMapping
	@ApiOperation(value	= "查询会员预存款余额")
	public Double get()	{
		Buyer buyer = UserContext.getBuyer();
		return depositeManager.getDeposite(buyer.getUid());
	}

	@GetMapping(value = "/cashier")
	@ApiOperation(value	= "获取预存款相关，收银台使用")
	public MemberDepositeVO getDepositeVO()	{
		Buyer buyer = UserContext.getBuyer();
		return depositeManager.getDepositeVO(buyer.getUid());
	}
	@GetMapping(value = "/check")
	@ApiOperation(value	= "检测会员是否设置过支付密码,会员中心设置或者修改密码时使用")
	public Boolean checkPassword() {
		Buyer buyer = UserContext.getBuyer();
		return depositeManager.getDepositeVO(buyer.getUid()).getIsPwd();
	}

	@ApiOperation(value = "获取账户信息")
	@GetMapping("info")
	public String getMemberInfo() {
		Buyer buyer = UserContext.getBuyer();
		//对会员状态进行校验
		Member member = memberManager.getModel(buyer.getUid());
		if (!member.getDisabled().equals(0)) {
			throw new ServiceException(MemberErrorCode.E107.code(), "当前账号已经禁用，请联系管理员");
		}
		//对获得的会员信息进行处理
		String mobile = member.getMobile();

		//对用户名的处理
		String name = member.getUname();

		//将数据组织好json格式返回
		String uuid = StringUtil.getUUId();

		Map map = new HashMap(16);
        map.put("uname", name.substring(0, 1) + "***" + name.substring(name.length() - 1, name.length()));
        mobile = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
		map.put("mobile", mobile);
		map.put("uuid", uuid);
		cache.put(uuid, member, javashopConfig.getSmscodeTimout());
		return JsonUtil.objectToJson(map);

	}

	@GetMapping(value = "/check/smscode")
	@ApiOperation(value = "验证修改密码验证码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sms_code", value = "验证码", required = true, dataType = "String", paramType = "query")
	})
	public String checkUpdatePwdCode(@Valid @ApiIgnore @NotEmpty(message = "验证码不能为空") String smsCode) {
		Buyer buyer = UserContext.getBuyer();
		Member member = memberManager.getModel(buyer.getUid());
		if (member == null || StringUtil.isEmpty(member.getMobile())) {
			throw new ServiceException(MemberErrorCode.E114.code(), "当前会员未绑定手机号");
		}
		boolean isPass = smsClient.valid(SceneType.SET_PAY_PWD.name(), member.getMobile(), smsCode);
		if (!isPass) {
			throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码不正确");
		}
		return null;

	}

	@PostMapping(value = "/set-pwd")
	@ApiOperation(value	= "设置支付密码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "password", value = "支付密码", required = true, dataType = "String", paramType = "query")
	})
	public void setPwd(@Pattern(regexp = "[a-fA-F0-9]{32}", message = "密码格式不正确") String password)	{
		Buyer buyer = UserContext.getBuyer();
		Member member = memberManager.getModel(buyer.getUid());
		String str = StringUtil.toString(cache.get(CachePrefix.MOBILE_VALIDATE.getPrefix() + "_" + SceneType.SET_PAY_PWD.name() + "_" + member.getMobile()));
		if (StringUtil.isEmpty(str)) {
			throw new ServiceException(MemberErrorCode.E115.code(), "请先对当前用户进行身份校验");
		}
		this.depositeManager.setDepositePwd(buyer.getUid(),password);
	}

	@PostMapping(value = "/smscode")
	@ApiOperation(value = "发送验证码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "uuid", value = "uuid客户端的唯一标识", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "captcha", value = "图片验证码", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "scene", value = "业务类型", required = true, dataType = "String", paramType = "query")
	})
	public String sendSmsCode( String uuid) {
		//参数验证（验证图片验证码或滑动验证参数等）
		this.validatorClient.validate();
		Member member = (Member) cache.get(uuid);
		if (member != null) {
			passportManager.sendSetPayPwdSmsCode(member.getMobile());
			return javashopConfig.getSmscodeTimout() / 60 + "";
		}
		throw new ServiceException(MemberErrorCode.E119.code(), "请先对当前用户进行身份校验");
	}




}
