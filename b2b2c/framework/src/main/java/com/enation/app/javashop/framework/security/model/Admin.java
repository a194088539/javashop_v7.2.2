package com.enation.app.javashop.framework.security.model;

import java.util.List;

/**
 * 管理员角色
 *
 * @author zh
 * @version v7.0
 * @date 18/6/27 上午10:09
 * @since v7.0
 */

public class Admin extends User {

    /**
     * 是否是超级管理员
     */
    private Integer founder;


    /**
     * 角色
     */
    private List<String> roles;


    public Admin() {
    }


    public Integer getFounder() {
        return founder;
    }

    public void setFounder(Integer founder) {
        this.founder = founder;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "founder=" + founder +
                ", roles=" + roles +
                "} " + super.toString();
    }
}
