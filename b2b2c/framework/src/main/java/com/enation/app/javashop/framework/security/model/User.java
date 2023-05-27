package com.enation.app.javashop.framework.security.model;

import com.enation.app.javashop.framework.auth.AuthUser;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户
 * Created by kingapex on 2018/3/8.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 2018/3/8
 */
public class User implements AuthUser {

    /**
     * 会员id
     */
    private Long uid;

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 用户名
     */
    private String username;
    /**
     * 角色
     */
    private List<String> roles;

    public User() {
        roles = new ArrayList<>();
    }

    /**
     * 为用户定义角色
     *
     * @param roles 角色集合
     */
    public void add(String... roles) {
        for (String role : roles) {
            this.roles.add(role);
        }
    }


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public List<String> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<String> roles) {
        this.roles= roles;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }
}
