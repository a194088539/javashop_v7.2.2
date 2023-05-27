package com.enation.app.javashop.service.shop.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.member.ShopMenuMapper;
import com.enation.app.javashop.model.shop.dos.ShopMenu;
import com.enation.app.javashop.model.shop.vo.ShopMenuVO;
import com.enation.app.javashop.model.shop.vo.ShopMenusVO;
import com.enation.app.javashop.model.system.dos.Menu;
import com.enation.app.javashop.service.shop.ShopMenuManager;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单管理店铺业务类
 *
 * @author zh
 * @version v7.0.0
 * @since v7.0.0
 * 2018-08-03 17:17:26
 */
@Service
public class ShopMenuManagerImpl implements ShopMenuManager {

    @Autowired
    private ShopMenuMapper shopMenuMapper;

    @Override
    public WebPage list(long page, long pageSize) {
        //获取店铺菜单分页列表数据
        IPage<ShopMenu> iPage = shopMenuMapper.selectPage(new Page<>(page, pageSize), new QueryWrapper<>());
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShopMenu add(ShopMenuVO shopMenuVO) {
        //对菜单的唯一标识做校验
        ShopMenu valMenu = this.getMenuByIdentifier(shopMenuVO.getIdentifier());
        if (valMenu != null) {
            throw new ServiceException(SystemErrorCode.E913.code(), "菜单唯一标识重复");
        }
        //判断父级菜单是否有效
        ShopMenu parentMenu = this.getModel(shopMenuVO.getParentId());
        if (shopMenuVO.getParentId()!=0 && parentMenu == null) {
            throw new ResourceNotFoundException("父级菜单不存在");
        }
        //校验菜单级别是否超出限制
        if (shopMenuVO.getParentId()!=0 && parentMenu.getGrade() >= 3) {
            throw new ServiceException(SystemErrorCode.E914.code(), "菜单级别最多为3级");
        }

        //新建店铺菜单对象
        ShopMenu menu = new ShopMenu();
        //复制菜单属性
        BeanUtil.copyProperties(shopMenuVO, menu);
        //设置菜单删除状态为未删除 0：未删除，1：已删除
        menu.setDeleteFlag(0);
        //添加店铺菜单数据
        shopMenuMapper.insert(menu);
        return this.updateMenu(menu);
    }

    /**
     * 执行修改菜单操作
     *
     * @param menu 菜单对象
     * @return 菜单对象
     */
    private ShopMenu updateMenu(ShopMenu menu) {
        //判断父级菜单是否有效
        ShopMenu parentMenu = this.getModel(menu.getParentId());
        if (menu.getParentId()!=0 && parentMenu == null) {
            throw new ResourceNotFoundException("父级菜单不存在");
        }
        //校验菜单级别是否超出限制
        if (menu.getParentId()!=0 && parentMenu.getGrade() >= 3) {
            throw new ServiceException(SystemErrorCode.E914.code(), "菜单级别最多为3级");
        }
        String menuPath = null;
        //判断菜单是否为顶级菜单，然后分别设置菜单标识（父id等于0证明是顶级菜单）
        if (menu.getParentId() == 0) {
            menuPath = "," + menu.getId() + ",";
        } else {
            menuPath = parentMenu.getPath() + menu.getId() + ",";
        }
        String subMenu = menuPath.substring(0, menuPath.length() - 1);
        String[] menus = subMenu.substring(1).split(",");
        //设置菜单等级
        menu.setGrade(menus.length);
        //设置菜单标识
        menu.setPath(menuPath);
        //修改菜单数据
        shopMenuMapper.updateById(menu);
        return menu;
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ShopMenu edit(ShopMenu shopMenu, Long id) {
        //校验当前菜单是否存在
        ShopMenu valMenu = this.getModel(id);
        if (valMenu == null) {
            throw new ResourceNotFoundException("当前菜单不存在");
        }
        //校验菜单唯一标识重复
        valMenu = this.getMenuByIdentifier(shopMenu.getIdentifier());
        if (valMenu != null && !valMenu.getId().equals(id)) {
            throw new ServiceException(SystemErrorCode.E913.code(), "菜单唯一标识重复");
        }
        shopMenu.setId(id);
        //执行修改
        return this.updateMenu(shopMenu);
    }

    @Override
    @Transactional(value = "memberTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        //根据ID获取店铺菜单信息并校验
        ShopMenu menu = this.getModel(id);
        if (menu == null) {
            throw new ResourceNotFoundException("当前菜单不存在");
        }


        //新建店铺菜单对象
        ShopMenu shopMenu = new ShopMenu();
        //设置菜单状态为删除状态
        shopMenu.setDeleteFlag(-1);
        //设置菜单主键ID
        shopMenu.setId(id);
        //修改店铺菜单信息
        shopMenuMapper.updateById(shopMenu);

        //新建修改条件包装器
        UpdateWrapper<ShopMenu> wrapper = new UpdateWrapper<>();
        //修改菜单状态为删除状态
        wrapper.set("delete_flag", -1);
        //以菜单父id作为修改条件
        wrapper.eq("parent_id", id);
        //修改子菜单为删除状态
        shopMenuMapper.update(new ShopMenu(), wrapper);

    }

    @Override
    public ShopMenu getModel(Long id) {
        return shopMenuMapper.selectById(id);
    }

    @Override
    public List<ShopMenusVO> getMenuTree(Long id) {
        //新建查询条件包装器
        QueryWrapper<ShopMenu> wrapper = new QueryWrapper<>();
        //以删除状态为正常状态做为查询条件 0：正常，-1：删除
        wrapper.eq("delete_flag", 0);
        //按id倒序排序
        wrapper.orderByDesc("id");
        //获取店铺菜单信息集合
        List<ShopMenusVO> menuList = shopMenuMapper.selectShopMenusListVo(wrapper);

        List<ShopMenusVO> topMenuList = new ArrayList<ShopMenusVO>();
        //循环结果集
        for (ShopMenusVO menu : menuList) {
            //判断是否为顶级菜单，如果是则设置下级菜单信息
            if (menu.getParentId().compareTo(id) == 0) {
                List<ShopMenusVO> children = this.getChildren(menuList, menu.getId());
                menu.setChildren(children);
                topMenuList.add(menu);
            }
        }
        return topMenuList;
    }

    /**
     * 在一个集合中查找子
     *
     * @param menuList 所有菜单集合
     * @param parentid 父id
     * @return 找到的子集合
     */
    private List<ShopMenusVO> getChildren(List<ShopMenusVO> menuList, Long parentid) {
        List<ShopMenusVO> children = new ArrayList<ShopMenusVO>();
        for (ShopMenusVO menu : menuList) {
            if (menu.getParentId().compareTo(parentid) == 0) {
                menu.setChildren(this.getChildren(menuList, menu.getId()));
                children.add(menu);
            }
        }
        return children;
    }

    @Override
    public ShopMenu getMenuByIdentifier(String identifier) {
        //新建查询条件包装器
        QueryWrapper<ShopMenu> wrapper = new QueryWrapper<>();
        //以删除状态为正常状态做为查询条件 0：正常，-1：删除
        wrapper.eq("delete_flag", 0);
        //以菜单唯一标识作为查询条件
        wrapper.eq("identifier", identifier);
        return shopMenuMapper.selectOne(wrapper);

    }
}
