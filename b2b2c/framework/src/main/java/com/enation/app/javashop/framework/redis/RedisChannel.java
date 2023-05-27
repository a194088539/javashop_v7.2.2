package com.enation.app.javashop.framework.redis;

/**
 * redis消息通道
 *
 * @author fk
 * @version 2.0
 * @since 7.1.5
 * 2019-09-07 18：00
 */
public class RedisChannel {


    /**
     * 敏感词消息
     */
    public final static String SENSITIVE_WORDS = "SENSITIVE_WORDS";

    /**
     * 用户禁用消息（管理员、卖家，买家）
     * 应用收到此消息后，应时该用户退出
     */
    public final static String USER_DISABLE = "USER_DISABLE";




}
