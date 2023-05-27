package com.enation.app.javashop.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.util.PageConvert;
import com.enation.app.javashop.mapper.system.MenuMapper;
import com.enation.app.javashop.model.errorcode.SystemErrorCode;
import com.enation.app.javashop.model.system.dos.Menu;
import com.enation.app.javashop.model.system.vo.MenuVO;
import com.enation.app.javashop.model.system.vo.MenusVO;
import com.enation.app.javashop.service.system.MenuManager;
import com.enation.app.javashop.framework.database.DaoSupport;
import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.framework.exception.ResourceNotFoundException;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.util.BeanUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 菜单管理业务类
 *
 * @author zh
 * @version v7.0
 * @since v7.0.0
 * 2018-06-19 09:46:02
 */
@Service
public class MenuManagerImpl implements MenuManager {

    @Autowired
    private MenuMapper menuMapper;


    @Override
    public WebPage list(long page, long pageSize) {

        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        IPage<Menu> iPage = menuMapper.selectPage(new Page<>(page,pageSize), wrapper);
        return PageConvert.convert(iPage);
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Menu add(MenuVO menuVO) {
        //对菜单的唯一标识做校验
        Menu valMenu = this.getMenuByIdentifier(menuVO.getIdentifier());
        if (valMenu != null) {
            throw new ServiceException(SystemErrorCode.E913.code(), "菜单唯一标识重复");
        }
        //判断父级菜单是否有效
        Menu parentMenu = this.getModel(menuVO.getParentId());
        if (menuVO.getParentId().longValue() != 0 && parentMenu == null) {
            throw new ResourceNotFoundException("父级菜单不存在");
        }
        //校验菜单级别是否超出限制
        if (menuVO.getParentId().longValue() != 0 && parentMenu.getGrade() >= 3) {
            throw new ServiceException(SystemErrorCode.E914.code(), "菜单级别最多为3级");
        }

        //对菜单名称进行重复校验
        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("title", menuVO.getTitle());
        Menu menuResult = menuMapper.selectOne(wrapper);

        if (menuResult != null && menuResult.getDeleteFlag().equals(-1)) {
            BeanUtil.copyProperties(menuVO, menuResult);
            menuResult.setDeleteFlag(0);
            return this.updateMenu(menuResult);
        } else if (menuResult != null) {
            throw new ServiceException(SystemErrorCode.E925.code(), "菜单名称重复");
        } else {
            //执行保存操作
            Menu menu = new Menu();
            BeanUtil.copyProperties(menuVO, menu);
            menu.setDeleteFlag(0);

            menuMapper.insert(menu);
            menu.setId(menu.getId());

            return this.updateMenu(menu);
        }
    }

    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Menu edit(Menu menu, Long id) {
        //校验当前菜单是否存在
        Menu valMenu = this.getModel(id);
        if (valMenu == null) {
            throw new ResourceNotFoundException("当前菜单不存在");
        }
        //校验菜单唯一标识重复
        valMenu = this.getMenuByIdentifier(menu.getIdentifier());
        if (valMenu != null && !valMenu.getId().equals(id)) {
            throw new ServiceException(SystemErrorCode.E913.code(), "菜单唯一标识重复");
        }
        //对菜单名称进行重复校验
        boolean bool = false;

        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("title", menu.getTitle());
        List<Menu> menus = menuMapper.selectList(wrapper);

        for (Menu me : menus) {
            if (!me.getId().equals(id)) {
                bool = true;
                continue;
            }
        }
        if (bool) {
            throw new ServiceException(SystemErrorCode.E925.code(), "菜单名称重复");
        }
        menu.setId(id);
        //执行修改
        return this.updateMenu(menu);
    }

    /**
     * 执行修改菜单操作
     *
     * @param menu 菜单对象
     * @return 菜单对象
     */
    private Menu updateMenu(Menu menu) {
        //判断父级菜单是否有效
        Menu parentMenu = this.getModel(menu.getParentId());
        if (menu.getParentId().longValue() != 0 && parentMenu == null) {
            throw new ResourceNotFoundException("父级菜单不存在");
        }
        //校验菜单级别是否超出限制
        if (menu.getParentId().longValue() != 0 && parentMenu.getGrade() >= 3) {
            throw new ServiceException(SystemErrorCode.E914.code(), "菜单级别最多为3级");
        }
        String menuPath = null;
        if (menu.getParentId().longValue() == 0) {
            menuPath = "," + menu.getId() + ",";
        } else {
            menuPath = parentMenu.getPath() + menu.getId() + ",";
        }
        String subMenu = menuPath.substring(0, menuPath.length() - 1);
        String[] menus = subMenu.substring(1).split(",");
        menu.setGrade(menus.length);
        menu.setPath(menuPath);

        menuMapper.updateById(menu);
        return menu;
    }


    @Override
    @Transactional(value = "systemTransactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(Long id) {
        Menu menu = this.getModel(id);
        if (menu == null) {
            throw new ResourceNotFoundException("当前菜单不存在");
        }

        Menu menuu = new Menu();
        UpdateWrapper<Menu> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",id);
        menuu.setDeleteFlag(-1);
        menuMapper.update(menuu,wrapper);
        wrapper.eq("parent_id",id);
        menuu.setDeleteFlag(-1);
        menuMapper.update(menuu,wrapper);

        //复杂的in语句，分布式事务不支持，故改成查询循环修改 add by fk
        //this.systemDaoSupport.execute("update es_menu as e1 set e1.delete_flag = -1 where e1.parent_id in ( select em.id from (select * from es_menu) as em where  em.parent_id =  ?)", id);

        List<Map> list = menuMapper.queryForId(id);
        if (StringUtil.isNotEmpty(list)) {
            for (Map map : list) {

                Long childId = (Long)map.get("id");
                Menu menuuu = new Menu();
                UpdateWrapper<Menu> wrapperr = new UpdateWrapper<>();
                menuuu.setDeleteFlag(-1);
                wrapperr.eq("id",childId);
                menuMapper.update(menuuu,wrapperr);

//                Long childId = (Long)map.get("id");
//                this.systemDaoSupport.execute("update es_menu set delete_flag = -1 where id = ?", childId);
            }
        }

    }

    @Override
    public Menu getModel(Long id) {

        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id).eq("delete_flag", 0);
        return menuMapper.selectOne(wrapper);
    }


    @Override
    public List<MenusVO> getMenuTree(Long id) {

        List<MenusVO> menuList = menuMapper.queryForVo();

        List<MenusVO> topMenuList = new ArrayList<MenusVO>();
        for (MenusVO menu : menuList) {
            if (menu.getParentId().compareTo(id) == 0) {
                List<MenusVO> children = this.getChildren(menuList, menu.getId());
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
    private List<MenusVO> getChildren(List<MenusVO> menuList, Long parentid) {
        List<MenusVO> children = new ArrayList<MenusVO>();
        for (MenusVO menu : menuList) {
            if (menu.getParentId().compareTo(parentid) == 0) {
                menu.setChildren(this.getChildren(menuList, menu.getId()));
                children.add(menu);
            }
        }
        return children;
    }

    @Override
    public Menu getMenuByIdentifier(String identifier) {

        QueryWrapper<Menu> wrapper = new QueryWrapper<>();
        wrapper.eq("delete_flag", 0).eq("identifier", identifier);
        return menuMapper.selectOne(wrapper);
    }
}
