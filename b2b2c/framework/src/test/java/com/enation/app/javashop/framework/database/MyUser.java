package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.auth.AuthUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-06-24
 */

public class MyUser implements AuthUser {

    private Long uid;
    private String username;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @Override
    public List<String> getRoles() {
        List roles = new ArrayList();
        roles.add("buyer");
        roles.add("seller");
        return roles;
    }

    @Override
    public void setRoles(List<String> roles) {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MyUser user = (MyUser) o;

        return getUsername() != null ? getUsername().equals(user.getUsername()) : user.getUsername() == null;

    }

    @Override
    public int hashCode() {
        return getUsername() != null ? getUsername().hashCode() : 0;
    }
}
