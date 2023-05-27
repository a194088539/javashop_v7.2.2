package com.enation.app.javashop.framework.auth;

/**
 * Token创建接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-06-21
 */
public interface TokenCreater {


    /**
     * 创建token
     * @param user 用户
     * @return token
     */
    Token create(AuthUser user);

}
