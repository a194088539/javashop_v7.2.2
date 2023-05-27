package com.enation.app.javashop.api.manager.system;

import com.enation.app.javashop.service.base.service.ValidatorManager;
import com.enation.app.javashop.model.errorcode.MemberErrorCode;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.dos.AdminUser;
import com.enation.app.javashop.model.system.vo.AdminLoginVO;
import com.enation.app.javashop.model.system.vo.AdminUserVO;
import com.enation.app.javashop.service.system.AdminUserManager;
import com.enation.app.javashop.framework.context.user.AdminUserContext;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 平台管理员控制器
 *
 * @author zh
 * @version v7.0
 * @since v7.0.0
 * 2018-06-20 20:38:26
 */
@RestController
@RequestMapping("/admin/systems/admin-users")
@Api(description = "平台管理员相关API")
@Validated
public class AdminUserManagerController {

    @Autowired
    private AdminUserManager adminUserManager;
    @Autowired
    private ValidatorManager validatorManager;


    @GetMapping("/login")
    @ApiOperation(value = "用户名（手机号）/密码登录API")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "query")
    })
    public AdminLoginVO login(@NotEmpty(message = "用户名不能为空") String username, @NotEmpty(message = "密码不能为空") String password) {
        //验证登录参数是否正确
        this.validatorManager.validate();

        return adminUserManager.login(username, password);
    }

    @ApiOperation(value = "刷新token")
    @PostMapping("/token")
    @ApiImplicitParam(name = "refresh_token", value = "刷新token", required = true, dataType = "String", paramType = "query")
    public String refreshToken(@ApiIgnore @NotEmpty(message = "刷新token不能为空") String refreshToken) {
        try {
            return adminUserManager.exchangeToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ServiceException(MemberErrorCode.E109.code(), "当前token已经失效");
        }
    }

    @PutMapping
    @ApiOperation(value = "修改管理员密码及头像", response = AdminUser.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "face", value = "头像", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "old_passwprd", value = "原密码", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "新密码", required = false, dataType = "String", paramType = "query")
    })
    public AdminUser edit(@NotEmpty(message = "管理员头像不能为空") String face, @ApiIgnore String oldPasswprd, String password) {
        Long uid = AdminUserContext.getAdmin().getUid();
        AdminUser adminUser = this.adminUserManager.getModel(uid);
        if (adminUser == null) {
            throw new ResourceNotFoundException("当前管理员不存在");
        }
        AdminUserVO adminUserVO = new AdminUserVO();
        BeanUtil.copyProperties(adminUser, adminUserVO);

        //校验密码
        if (!StringUtil.isEmpty(oldPasswprd) && StringUtil.isEmpty(password)) {
            throw new ServiceException(SystemErrorCode.E923.code(), "新密码不能为空");
        }
        if (StringUtil.isEmpty(oldPasswprd) && !StringUtil.isEmpty(password)) {
            throw new ServiceException(SystemErrorCode.E922.code(), "原始密码不能为空");
        }
        if (!StringUtil.isEmpty(oldPasswprd) && !StringUtil.isEmpty(password)) {
            String dbOldPassword = StringUtil.md5(oldPasswprd + adminUser.getUsername().toLowerCase());
            if (!dbOldPassword.equals(adminUser.getPassword())) {
                throw new ServiceException(SystemErrorCode.E921.code(), "原密码错误");
            }
            adminUserVO.setPassword(password);
        } else {
            adminUserVO.setPassword("");
        }
        adminUserVO.setFace(face);
        AdminUser upAdminUser = adminUserManager.edit(adminUserVO, adminUser.getId());
        this.adminUserManager.logout(uid);
        return upAdminUser;
    }

    @ApiOperation(value = "注销管理员登录")
    @PostMapping(value = "/logout")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "管理员id", dataType = "int", paramType = "query", required = true)
    })
    public String loginOut(@NotNull(message = "管理员id不能为空") Long uid) {
        this.adminUserManager.logout(uid);
        return null;
    }

}
