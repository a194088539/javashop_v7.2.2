package com.enation.app.javashop.framework.security;

import com.enation.app.javashop.framework.auth.AuthUser;
import com.enation.app.javashop.framework.auth.Token;
import com.enation.app.javashop.framework.auth.TokenParseException;

/**
 * token业务管理接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019/12/25
 */
public interface TokenManager {


    /**
     * 创建token
     * @param user
     * @return
     */
    Token create(AuthUser user);

    /**
     * 解析token
     * @param token
     * @return 用户对象
     */
    <T>  T parse(Class<T> clz, String token) throws TokenParseException;
}
