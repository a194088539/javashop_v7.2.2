package com.enation.app.javashop.framework.auth;

/**
 * Token 解析器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-06-21
 */
public interface TokenParser {

    /**
     * 解析token
     * @param token
     * @return 用户对象
     */
    <T>  T parse(Class<T> clz, String token) throws TokenParseException;


}
