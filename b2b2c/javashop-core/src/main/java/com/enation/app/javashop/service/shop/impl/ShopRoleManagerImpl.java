package com.enation.app.javashop.service.shop.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.ClerkMapper;
import com.enation.app.javashop.mapper.member.ShopRoleMapper;
import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.model.errorcode.ShopErrorCode;
import com.enation.app.javashop.model.shop.dos.ClerkDO;
import com.enation.app.javashop.model.shop.dos.ShopRole;
import com.enation.app.javashop.model.shop.vo.ShopMenus;
import com.enation.app.javashop.model.shop.vo.ShopRoleVO;
import com.enation.app.javashop.service.shop.ShopRoleManager;
import com.enation.app.javashop.model.system.dos.RoleDO;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺角色业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-02 15:22:20
 */
@Service
public class ShopRoleManagerImpl implements ShopRoleManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ShopRoleMapper shopRoleMapper;

    @Autowired
    private ClerkMapper clerkMapper;

    @Autowired
    private Cache cache;


    @Override
    public WebPage list(long page, long pageSize) {
        //新建查询条件包装器
        QueryWrapper<ShopRole> wrapper = new QueryWrapper<>();
        //设置要查询的信息 角色ID、角色名称和角色描述
        wrapper.select("role_id", "role_name", "role_describe");
        //以店铺id为查询条件
        wrapper.eq("shop_id", UserContext.getSeller().getSellerId());
        //获取店铺角色分页列表信息
        IPage<ShopRole> iPage = shopRoleMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShopRoleVO add(ShopRoleVO shopRoleVO) {
        //获取当前登录的店铺ID
        Long sellerId = UserContext.getSeller().getSellerId();
        //新建查询条件包装器
        QueryWrapper<ShopRole> wrapper = new QueryWrapper<>();
        //以店铺id为查询条件
        wrapper.eq("shop_id", sellerId);
        //以角色名称为查询条件
        wrapper.eq("role_name", shopRoleVO.getRoleName());
        //查询店铺角色信息数量
        int count = shopRoleMapper.selectCount(wrapper);
        //首先校验角色名称是否已经存在,如果存在抛出异常
        if (count > 0) {
            logger.debug("试图添加店铺角色时发现已经存在要添加的角色名称：【" + shopRoleVO.getRoleName() + "】，店铺id为：【" + sellerId + "】");
            throw new ServiceException(ShopErrorCode.E228.code(), ShopErrorCode.E228.getDescribe());
        }

        //新建店铺角色对象
        ShopRole shopRole = new ShopRole();
        //设置店铺角色名称
        shopRole.setRoleName(shopRoleVO.getRoleName());
        //设置店铺角色信息
        shopRole.setAuthIds(JsonUtil.objectToJson(shopRoleVO.getMenus()));
        //设置店铺角色描述
        shopRole.setRoleDescribe(shopRoleVO.getRoleDescribe());
        //设置店铺ID
        shopRole.setShopId(sellerId);
        //店铺角色信息入库
        shopRoleMapper.insert(shopRole);

        //删除缓存中店铺角色信息
        cache.remove(CachePrefix.SHOP_URL_ROLE.getPrefix() + sellerId);
        return shopRoleVO;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShopRoleVO edit(ShopRoleVO shopRoleVO, Long id) {
        //根据ID获取店铺角色信息并校验
        ShopRole shopRole = this.getModel(id);
        if (shopRole == null) {
            throw new ResourceNotFoundException("此角色不存在");
        }

        //获取店铺角色名称
        String roleName = shopRoleVO.getRoleName();
        //获取当前登录的商家ID
        long sellerId = UserContext.getSeller().getSellerId();
        //新建查询条件包装器
        QueryWrapper<ShopRole> wrapper = new QueryWrapper<>();
        //以店铺id为查询条件
        wrapper.eq("shop_id", sellerId);
        //以角色名称为查询条件
        wrapper.eq("role_name", roleName);
        //以角色ID不等于当前角色ID为查询条件，也就是此处要将非本角色的过滤
        wrapper.ne("role_id", id);
        //获取店铺角色数量
        int count = shopRoleMapper.selectCount(wrapper);
        //校验角色名称是否已经存在,如果存在抛出异常
        if (count > 0) {
            logger.debug("试图修改店铺角色时发现已经存在要添加的角色名称：【" + roleName + "】，店铺id为：【" + sellerId + "】");
            throw new ServiceException(ShopErrorCode.E228.code(), ShopErrorCode.E228.getDescribe());
        }

        //修改角色名称
        shopRole.setRoleName(roleName);
        //修改角色配置信息
        shopRole.setAuthIds(JsonUtil.objectToJson(shopRoleVO.getMenus()));
        //修改角色描述
        shopRole.setRoleDescribe(shopRoleVO.getRoleDescribe());
        //修改店铺ID
        shopRole.setShopId(sellerId);
        //修改店铺角色信息
        shopRoleMapper.updateById(shopRole);

        //删除缓存中角色所拥有的菜单权限
        cache.remove(CachePrefix.SHOP_URL_ROLE.getPrefix() + sellerId);
        return shopRoleVO;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //根据ID获取店铺角色信息并校验
        ShopRole shopRole = this.getModel(id);
        if (shopRole == null) {
            throw new ResourceNotFoundException("此角色不存在");
        }

        //新建查询条件包装器
        QueryWrapper<ClerkDO> wrapper = new QueryWrapper<>();
        //以店铺id为查询条件
        wrapper.eq("shop_id", UserContext.getSeller().getSellerId());
        //以角色id为查询条件
        wrapper.eq("role_id", id);
        //根据条件查询出店铺店员信息集合
        List<ClerkDO> clerks = clerkMapper.selectList(wrapper);
        //校验此角色是否已经被使用
        if (clerks.size() > 0) {
            throw new ServiceException(ShopErrorCode.E229.code(), "此角色已经被使用");
        }

        //删除缓存中的店铺角色信息
        cache.remove(CachePrefix.SHOP_URL_ROLE.getPrefix() + UserContext.getSeller().getSellerId());
        //删除店铺角色
        shopRoleMapper.deleteById(id);
    }

    @Override
    public ShopRole getModel(Long id) {
        return shopRoleMapper.selectById(id);
    }

    @Override
    public Map<String, List<String>> getRoleMap(Long sellerId) {
        Map<String, List<String>> roleMap = new HashMap<>(16);

        //新建查询条件包装器
        QueryWrapper<ShopRole> wrapper = new QueryWrapper<>();
        //以店铺id为查询条件
        wrapper.eq("shop_id", sellerId);
        //获取店铺角色信息集合
        List<ShopRole> roles = shopRoleMapper.selectList(wrapper);
        for (int i = 0; i < roles.size(); i++) {
            List<ShopMenus> menusList = JsonUtil.jsonToList(roles.get(i).getAuthIds(), ShopMenus.class);
            if (menusList.size() > 0) {
                List<String> authList = new ArrayList<>();
                //递归查询角色所拥有的菜单权限
                this.getChildren(menusList, authList);
                roleMap.put(roles.get(i).getRoleName(), authList);
                cache.put(CachePrefix.SHOP_URL_ROLE.getPrefix()+ sellerId, roleMap);
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
    private void getChildren(List<ShopMenus> menuList, List<String> authList) {
        for (ShopMenus menus : menuList) {
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
        //根据角色ID获取店铺角色信息
        ShopRole shopRole = this.getModel(id);
        //非空校验与权限校验
        if (shopRole == null || !shopRole.getShopId().equals(UserContext.getSeller().getSellerId())) {
            throw new ResourceNotFoundException("此角色不存在");
        }
        List<ShopMenus> menusList = JsonUtil.jsonToList(shopRole.getAuthIds(), ShopMenus.class);
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
    private void reset(List<ShopMenus> menuList, List<String> authList) {
        for (ShopMenus menus : menuList) {
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
    public ShopRoleVO getRole(Long id) {
        return new ShopRoleVO(this.getModel(id));
    }
}
