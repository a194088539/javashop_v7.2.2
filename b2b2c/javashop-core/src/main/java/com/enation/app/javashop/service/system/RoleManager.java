package com.enation.app.javashop.service.system;

import com.enation.app.javashop.model.system.dos.RoleDO;
import com.enation.app.javashop.model.system.vo.RoleVO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;
import java.util.Map;


/**
 * 角色表业务层
 *
 * @author admin
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-17 16:48:27
 */
public interface RoleManager {

    /**
     * 查询角色表列表
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @param keyword 搜索关键字
     * @return WebPage
     */
    WebPage list(long page, long pageSize, String keyword);

    /**
     * 添加角色表
     *
     * @param role 角色表
     * @return Role 角色表
     */
    RoleVO add(RoleVO role);

    /**
     * 修改角色表
     *
     * @param role 角色表
     * @param id   角色表主键
     * @return Role 角色表
     */
    RoleVO edit(RoleVO role, Long id);

    /**
     * 删除角色表
     *
     * @param id 角色表主键
     */
    void delete(Long id);

    /**
     * 获取角色表
     *
     * @param id 角色表主键
     * @return Role  角色表
     */
    RoleDO getModel(Long id);

    /**
     * 获取角色表
     *
     * @param id 角色表主键
     * @return Role  角色表
     */
    RoleVO getRole(Long id);


    /**
     * 获取所有角色的权限对照表
     *
     * @return 权限对照表
     */
    Map<String, List<String>> getRoleMap();


    /**
     * 根据角色id获取所属菜单
     *
     * @param id 角色id
     * @return 菜单唯一标识
     */
    List<String> getRoleMenu(Long id);


}
