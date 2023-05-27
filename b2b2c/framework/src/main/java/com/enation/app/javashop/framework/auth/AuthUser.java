package com.enation.app.javashop.framework.auth;

import java.util.List;

/**
 * 认证用户接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-06-21
 */
public interface AuthUser {

    List<String> getRoles();

    void setRoles(List<String> roles);
}
