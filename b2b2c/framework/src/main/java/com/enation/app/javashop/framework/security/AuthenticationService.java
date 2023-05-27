package com.enation.app.javashop.framework.security;

import com.enation.app.javashop.framework.security.message.UserDisableMsg;

import javax.servlet.http.HttpServletRequest;

/**
 * 认证业务类
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019/12/27
 */
public interface AuthenticationService {


    /**
     * 认证
     * @param req
     */
    void auth(HttpServletRequest req);


    /**
     * 用户被禁用事件
     * @param userDisableMsg
     */
    void userDisableEvent(UserDisableMsg userDisableMsg);

}
