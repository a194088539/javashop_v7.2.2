package com.enation.app.javashop.api.seller.shop;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.SceneType;
import com.enation.app.javashop.client.system.SmsClient;
import com.enation.app.javashop.client.system.ValidatorClient;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.member.dos.Member;
import com.enation.app.javashop.service.member.MemberManager;
import com.enation.app.javashop.service.member.MemberSecurityManager;
import com.enation.app.javashop.service.passport.PassportManager;
import com.enation.app.javashop.model.shop.dos.ClerkDO;
import com.enation.app.javashop.model.shop.dto.ClerkDTO;
import com.enation.app.javashop.model.shop.vo.ClerkAddVO;
import com.enation.app.javashop.model.shop.vo.ClerkShowVO;
import com.enation.app.javashop.model.shop.vo.ClerkVO;
import com.enation.app.javashop.service.shop.ClerkManager;
import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.exception.NoPermissionException;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.validation.annotation.Mobile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 店员控制器
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-04 18:48:39
 */
@RestController
@RequestMapping("/seller/shops/clerks")
@Api(description = "店员相关API")
public class ClerkSellerController {

    @Autowired
    private ClerkManager clerkManager;
    @Autowired
    private MemberManager memberManager;
    @Autowired
    private MemberSecurityManager memberSecurityManager;
    @Autowired
    private ValidatorClient validatorClient;
    @Autowired
    private PassportManager passportManager;
    @Autowired
    private SmsClient smsClient;
    @Autowired
    private JavashopConfig javashopConfig;


    @PostMapping(value = "/sms-code/{mobile}")
    @ApiOperation(value = "发送验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "path")
    })
    public String smsCode(@PathVariable("mobile") String mobile) {
        //参数验证（验证图片验证码或滑动验证参数等）
        this.validatorClient.validate();

        passportManager.sendSmsCode(mobile);
        return javashopConfig.getSmscodeTimout() / 60 + "";
    }

    @GetMapping("/check/{mobile}")
    @ApiOperation(value = "对手机号码进行校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "sms_code", value = "短信验证码", required = true, dataType = "String", paramType = "query")
    })
    public ClerkAddVO checkAddClerkMobile(@PathVariable("mobile") @Mobile String mobile, @ApiIgnore String smsCode) {
        boolean bool = smsClient.valid(SceneType.ADD_CLERK.name(), mobile, smsCode);
        if (!bool) {
            throw new ServiceException(MemberErrorCode.E107.code(), "短信验证码错误");
        }
        ClerkAddVO clerkAddVO = new ClerkAddVO();
        Member member = memberManager.getMemberByMobile(mobile);
        if (member != null) {
            //校验要添加的会员是否已经是店主
            if (member.getHaveShop().equals(1)) {
                throw new ServiceException(MemberErrorCode.E144.code(), "此账号已经拥有店铺");
            }
            //校验会员的有效性
            if (!member.getDisabled().equals(0)) {
                throw new ServiceException(MemberErrorCode.E124.code(), "此账号已经被禁用");
            }
            //校验此会员是否已经是店员
            ClerkDO clerk = clerkManager.getClerkByMemberId(member.getMemberId());
            if (clerk != null && !clerk.getShopId().equals(UserContext.getSeller().getSellerId())) {
                throw new ServiceException(MemberErrorCode.E145.code(), "此会员已经成为其他店铺店员");
            }
            if (clerk != null && clerk.getShopId().equals(UserContext.getSeller().getSellerId())) {
                throw new ServiceException(MemberErrorCode.E146.code(), "此会员已经是本店店员");
            }
            clerkAddVO.setResult("exist");
        } else {
            clerkAddVO.setResult("no exist");
        }
        clerkAddVO.setMobile(mobile);
        return clerkAddVO;

    }

    @ApiOperation(value = "添加老会员为店员", response = ClerkDO.class)
    @PostMapping("/old")
    public ClerkDO addOldMemberClerk(@Valid ClerkDTO clerkDTO) {
        return this.clerkManager.addOldMemberClerk(clerkDTO);
    }


    @ApiOperation(value = "查询店员列表", response = ClerkShowVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_no", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "page_size", value = "每页显示数量", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "disabled", value = "店员状态-1为禁用，0为正常", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String", paramType = "query")
    })
    @GetMapping
    public WebPage list(@ApiIgnore Long pageNo, @ApiIgnore Long pageSize, @ApiIgnore Integer disabled, @ApiIgnore String keyword) {
        return this.clerkManager.list(pageNo, pageSize, disabled, keyword);
    }


    @ApiOperation(value = "添加店员", response = ClerkDO.class)
    @PostMapping("/new")
    public ClerkDO addNewMemberClerk(@Valid ClerkVO clerkVO) {
        return this.clerkManager.addNewMemberClerk(clerkVO);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "删除店员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "要删除的店员主键", required = true, dataType = "int", paramType = "path")
    })
    public String delete(@PathVariable Long id) {
        this.clerkManager.delete(id);
        return "";
    }


    @PutMapping(value = "/{id}")
    @ApiOperation(value = "修改店员", response = ClerkDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "path")
    })
    public ClerkDO edit(@Valid ClerkVO clerkVO, @PathVariable Long id) {
        ClerkDO clerk = this.clerkManager.getModel(id);
        if (clerk == null || !clerk.getShopId().equals(UserContext.getSeller().getSellerId())) {
            throw new NoPermissionException("无权限");
        }
        //校验权限，超级管理员不能修改成为普通管理员，普通管理员也不能修改成为超级管理员
        boolean bool = (clerkVO.getRoleId()==0 && clerk.getRoleId()==0) || (clerkVO.getRoleId()!=0 && clerk.getRoleId()!=0);
        if (!bool) {
            throw new ServiceException(MemberErrorCode.E136.code(), "权限操作错误");
        }
        //如果密码不为空则修改店员密码
        if (!StringUtil.isEmpty(clerkVO.getPassword())) {
            this.memberSecurityManager.updatePassword(clerk.getMemberId(), clerkVO.getPassword());
        }
        //修改邮箱及手机号码
        Member member = this.memberManager.getModel(clerk.getMemberId());
        if (member == null) {
            throw new ResourceNotFoundException("当前会员不存在");
        }
        member.setEmail(clerkVO.getEmail());
        member.setMobile(clerkVO.getMobile());
        this.memberManager.edit(member, member.getMemberId());
        //修改店员
        clerk.setRoleId(clerkVO.getRoleId());
        clerk.setClerkName(clerkVO.getUname());
        this.clerkManager.edit(clerk, id);
        return clerk;
    }


    @PutMapping(value = "/{id}/recovery")
    @ApiOperation(value = "恢复店员", response = ClerkDO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "店员id", required = true, dataType = "int", paramType = "path")
    })
    public String recovery(@PathVariable Long id) {
        ClerkDO clerk = this.clerkManager.getModel(id);
        if (clerk == null || !clerk.getShopId().equals(UserContext.getSeller().getSellerId())) {
            throw new NoPermissionException("无权限");
        }
        //修改店员
        this.clerkManager.recovery(id);
        return "";
    }


}
