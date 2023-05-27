package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.AdminUserMapper;
import com.enation.app.javashop.mapper.system.RoleMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.system.dos.AdminUser;
import com.enation.app.javashop.service.system.RoleManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.dos.RoleDO;
import com.enation.app.javashop.model.system.vo.Menus;
import com.enation.app.javashop.model.system.vo.RoleVO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 角色表业务类
 *
 * @author kingapex
 * @version v1.0.0
 * @since v7.0.0
 * 2018-04-17 16:48:27
 */
@Service
public class RoleManagerImpl implements RoleManager {

    @Autowired
    private Cache cache;
    @Autowired
    private AdminUserMapper adminUserMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public WebPage list(long page, long pageSize, String keyword) {

        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.select("role_id","role_name","role_describe");
        wrapper.like(StringUtil.notEmpty(keyword),"role_name",keyword );
        wrapper.orderByDesc("role_id ");
        IPage<RoleDO> iPage = roleMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RoleVO add(RoleVO role) {

        this.checkRole(role);
        RoleDO roleDO = new RoleDO();
        roleDO.setRoleName(role.getRoleName());
        roleDO.setAuthIds(JsonUtil.objectToJson(role.getMenus()));
        roleDO.setRoleDescribe(role.getRoleDescribe());

        roleMapper.insert(roleDO);
        role.setRoleId(roleDO.getRoleId());

        //删除缓存中角色所拥有的菜单权限
        cache.remove(CachePrefix.ADMIN_URL_ROLE.getPrefix());
        return role;
    }

    /**
     * 检测角色信息是否合法
     * @param role
     */
    private void checkRole(RoleVO role) {

        Long id = role.getRoleId();

        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();

        //添加
        wrapper.eq(id == null,"role_name",role.getRoleName());

        wrapper.eq("role_name",role.getRoleName());
        wrapper.ne("role_id",id);


        List list = roleMapper.selectList(wrapper);

        if(list.size()>0){
            throw new ServiceException(SystemErrorCode.E924.code(),"角色名称重复");
        }
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RoleVO edit(RoleVO role, Long id) {
        //校验权限是否存在
        RoleDO roleDO = this.getModel(id);
        if (roleDO == null) {
            throw new ResourceNotFoundException("此角色不存在");
        }
        role.setRoleId(id);
        this.checkRole(role);

        roleDO.setRoleName(role.getRoleName());
        roleDO.setAuthIds(JsonUtil.objectToJson(role.getMenus()));
        roleDO.setRoleDescribe(role.getRoleDescribe());
        roleMapper.updateById(roleDO);
        role.setRoleId(id);
        //删除缓存中角色所拥有的菜单权限
        cache.remove(CachePrefix.ADMIN_URL_ROLE.getPrefix());
        return role;
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        RoleDO roleDO = this.getModel(id);
        if (roleDO == null) {
            throw new ResourceNotFoundException("此角色不存在");
        }

        //查看角色下是否有管理员，有管理员则不能删除
        QueryWrapper<AdminUser> wrapper = new QueryWrapper<>();
        wrapper.eq("role_id", id).ne("user_state", -1);
        List list = adminUserMapper.selectList(wrapper);

        if (StringUtil.isNotEmpty(list)) {
            throw new ServiceException(SystemErrorCode.E924.code(), "该角色下有管理员，请删除管理员后再删除角色");
        }

        roleMapper.deleteById(id);
    }

    @Override
    public RoleDO getModel(Long id) {

        RoleDO roleDO = roleMapper.selectById(id);
        if (roleDO == null){
            throw new ResourceNotFoundException("此角色不存在");
        }
        return roleDO;
    }

    @Override
    public Map<String, List<String>> getRoleMap() {

        Map<String, List<String>> roleMap = new HashMap<>(16);

        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        List<RoleDO> roles = roleMapper.selectList(wrapper);

        for (int i = 0; i < roles.size(); i++) {
            List<Menus> menusList = JsonUtil.jsonToList(roles.get(i).getAuthIds(), Menus.class);
            if (menusList != null && menusList.size() > 0) {
                List<String> authList = new ArrayList<>();
                //递归查询角色所拥有的菜单权限
                this.getChildren(menusList, authList);
                roleMap.put(roles.get(i).getRoleName(), authList);
                cache.put(CachePrefix.ADMIN_URL_ROLE.getPrefix(), roleMap);
            }
        }
        return roleMap;
    }

    /**
     * 递归将此角色锁拥有的菜单权限保存到list
     *
     * @param menuList 菜单集合
     * @param authList 权限组集合
     */
    private void getChildren(List<Menus> menuList, List<String> authList) {
        for (Menus menus : menuList) {
            //将此角色拥有的菜单权限放入list中
            if (menus.isChecked()) {
                authList.add(menus.getAuthRegular());
            }
            if (!menus.getChildren().isEmpty()) {
                getChildren(menus.getChildren(), authList);
            }
        }
    }

    @Override
    public List<String> getRoleMenu(Long id) {
        RoleDO roleDO = this.getModel(id);
        if (roleDO == null) {
            throw new ResourceNotFoundException("此角色不存在");
        }
        List<Menus> menusList = JsonUtil.jsonToList(roleDO.getAuthIds(), Menus.class);
        List<String> authList = new ArrayList<>();
        //筛选菜单
        this.reset(menusList, authList);
        return authList;
    }

    /**
     * 筛选checked为true的菜单
     *
     * @param menuList 菜单集合
     */
    private void reset(List<Menus> menuList, List<String> authList) {
        for (Menus menus : menuList) {
            //将此角色拥有的菜单权限放入list中
            if (menus.isChecked()) {
                authList.add(menus.getIdentifier());
            }
            if (!menus.getChildren().isEmpty()) {
                reset(menus.getChildren(), authList);
            }
        }
    }

    @Override
    public RoleVO getRole(Long id) {
        return new RoleVO(this.getModel(id));
    }
}

