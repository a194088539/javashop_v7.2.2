package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.redis.RedisChannel;
import com.enation.app.javashop.mapper.system.AdminUserMapper;
import com.enation.app.javashop.service.system.RoleManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.dos.AdminUser;
import com.enation.app.javashop.model.system.dos.RoleDO;
import com.enation.app.javashop.model.system.dto.AdminUserDTO;
import com.enation.app.javashop.model.system.vo.AdminLoginVO;
import com.enation.app.javashop.model.system.vo.AdminUserVO;
import com.enation.app.javashop.service.system.AdminUserManager;
import com.enation.app.javashop.framework.auth.Token;
import com.enation.app.javashop.framework.context.request.ThreadContextHolder;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.security.TokenManager;
import com.enation.app.javashop.framework.security.message.UserDisableMsg;
import com.enation.app.javashop.framework.security.model.Admin;
import com.enation.app.javashop.framework.security.model.Role;
import com.enation.app.javashop.framework.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 平台管理员业务类
 *
 * @author zh
 * @version v7.0
 * @since v7.0.0
 * 2018-06-20 20:38:26
 */
@Service
public class AdminUserManagerImpl implements AdminUserManager {

    @Autowired
    private RoleManager roleManager;
    @Autowired
    private AdminUserMapper adminUserMapper;

    @Override
    public WebPage list(long page, long pageSize, String keyword) {

        IPage<AdminUserDTO> iPage = adminUserMapper.selectPageDto(new Page<>(page, pageSize),keyword);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AdminUser add(AdminUserVO adminUserVO) {
        boolean bool = Pattern.matches("[a-fA-F0-9]{32}", adminUserVO.getPassword());
        if (!bool) {
            throw new ServiceException(SystemErrorCode.E917.code(), "密码格式不正确");
        }
        //校验用户名称是否重复
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", adminUserVO.getUsername()).eq("user_state", 0);
        AdminUser user = adminUserMapper.selectOne(wrapper);

        if (user != null) {
            throw new ServiceException(SystemErrorCode.E915.code(), "管理员名称重复");
        }
        //不是超级管理员的情况下再校验权限是否存在
        if (!adminUserVO.getFounder().equals(1)) {
            RoleDO roleDO = roleManager.getModel(adminUserVO.getRoleId());
            if (roleDO == null) {
                throw new ResourceNotFoundException("所属权限不存在");
            }
        }
        String password = adminUserVO.getPassword();
        AdminUser adminUser = new AdminUser();
        BeanUtil.copyProperties(adminUserVO, adminUser);
        adminUser.setPassword(StringUtil.md5(password + adminUser.getUsername().toLowerCase()));
        adminUser.setDateLine(DateUtil.getDateline());
        adminUser.setUserState(0);
        adminUserMapper.insert(adminUser);
        adminUser.setId(adminUser.getId());
        return adminUser;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AdminUser edit(AdminUserVO adminUserVO, Long id) {
        //对要修改的管理员是否存在进行校验
        AdminUser adminUser = this.getModel(id);
        if (adminUser == null) {
            throw new ResourceNotFoundException("当前管理员不存在");
        }
        //如果修改的是从超级管理员到普通管理员 需要校验此管理员是否是最后一个超级管理员
        if (adminUser.getFounder().equals(1) && !adminUserVO.getFounder().equals(1)) {

            QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
            wrapper.eq("founder", 1).eq("user_state", 0);
            List<AdminUser> adminUsers = adminUserMapper.selectList(wrapper);

            if (adminUsers.size() <= 1) {
                throw new ServiceException(SystemErrorCode.E916.code(), "必须保留一个超级管理员");
            }
        }
        if (!adminUserVO.getFounder().equals(1)) {
            RoleDO roleDO = roleManager.getModel(adminUserVO.getRoleId());
            if (roleDO == null) {
                throw new ResourceNotFoundException("所属权限不存在");
            }
        } else {
            adminUserVO.setRoleId(0L);
        }
        //管理员原密码
        String password = adminUser.getPassword();
        //对管理员是否修改密码进行校验
        if (!StringUtil.isEmpty(adminUserVO.getPassword())) {
            boolean bool = Pattern.matches("[a-fA-F0-9]{32}", adminUserVO.getPassword());
            if (!bool) {
                throw new ServiceException(SystemErrorCode.E917.code(), "密码格式不正确");
            }
            adminUserVO.setPassword(StringUtil.md5(adminUserVO.getPassword() + adminUser.getUsername().toLowerCase()));
        } else {
            adminUserVO.setPassword(password);
        }
        adminUserVO.setUsername(adminUser.getUsername());
        BeanUtil.copyProperties(adminUserVO, adminUser);
        adminUserMapper.updateById(adminUser);
        return adminUser;
    }
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //校验当前管理员是否存在
        AdminUser adminUser = this.getModel(id);
        if (adminUser == null) {
            throw new ResourceNotFoundException("当前管理员不存在");
        }
        //校验要删除的管理员是否是最后一个超级管理员
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("founder", 1).eq("user_state", 0);
        List<AdminUser> adminUsers = adminUserMapper.selectList(wrapper);

        if (adminUsers.size() <= 1 && adminUser.getFounder().equals(1)) {
            throw new ServiceException(SystemErrorCode.E916.code(), "必须保留一个超级管理员");
        }

        adminUser.setUserState(-1);
        adminUserMapper.updateById(adminUser);

        //发送redis订阅消息,通知各个节点此用户已经禁用
        UserDisableMsg userDisableMsg = new UserDisableMsg(id, Role.ADMIN, UserDisableMsg.ADD);
        String msgJson = JsonUtil.objectToJson(userDisableMsg);
        redisTemplate.convertAndSend(RedisChannel.USER_DISABLE, msgJson);

    }

    @Override
    public AdminUser getModel(Long id) {

        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("user_state", 0).eq("id", id);
        return adminUserMapper.selectOne(wrapper);
    }

    @Override
    public AdminLoginVO login(String name, String password) {

        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", name).eq("password", StringUtil.md5(password + name.toLowerCase())).eq("user_state", 0);
        AdminUser adminUser =  adminUserMapper.selectOne(wrapper);

        if (adminUser == null || !StringUtil.equals(adminUser.getUsername(), name)) {
            throw new ServiceException(SystemErrorCode.E918.code(), "管理员账号密码错误");
        }
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        adminLoginVO.setUid(adminUser.getId());
        adminLoginVO.setUsername(name);
        adminLoginVO.setDepartment(adminUser.getDepartment());
        adminLoginVO.setFace(adminUser.getFace());
        adminLoginVO.setRoleId(adminUser.getRoleId());
        adminLoginVO.setFounder(adminUser.getFounder());

        Token token = createToken(adminUser);

        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        adminLoginVO.setAccessToken(accessToken);
        adminLoginVO.setRefreshToken(refreshToken);
        return adminLoginVO;
    }

    @Override
    public String exchangeToken(String refreshToken) {
        if (refreshToken != null) {
            Admin admin = tokenManager.parse(Admin.class, refreshToken);

            Long uid = admin.getUid();

            //获取uuid
            String uuid = ThreadContextHolder.getHttpRequest().getHeader("uuid");
            //根据id获取管理员 校验当前管理员是否存在
            AdminUser adminUser = this.getModel(uid);
            if (adminUser == null) {
                throw new ResourceNotFoundException("当前管理员不存在");
            }

            Token token = createToken(adminUser);

            String newAccessToken = token.getAccessToken();
            String newRefreshToken = token.getRefreshToken();

            Map map = new HashMap(16);
            map.put("accessToken", newAccessToken);
            map.put("refreshToken", newRefreshToken);

            return JsonUtil.objectToJson(map);

        }
        throw new ResourceNotFoundException("当前管理员不存在");
    }

    @Autowired
    private TokenManager tokenManager;

    /**
     * 使用adminUser 创建token
     *
     * @param adminUser
     * @return
     */
    private Token createToken(AdminUser adminUser) {
        Admin admin = new Admin();
        admin.setUid(adminUser.getId());
        admin.setUsername(adminUser.getUsername());
        if (adminUser.getFounder().equals(1)) {
            admin.add("SUPER_ADMIN");
        } else {
            RoleDO roleDO = this.roleManager.getModel(adminUser.getRoleId());
            admin.add(roleDO.getRoleName());
        }

        return tokenManager.create(admin);

    }


    @Override
    public void logout(Long uid) {

    }
}
