package com.enation.app.javashop.framework.security.message;

import com.enation.app.javashop.framework.redis.RedisChannel;
import com.enation.app.javashop.framework.redis.redismq.RedisMsgReceiver;
import com.enation.app.javashop.framework.security.AuthenticationService;
import com.enation.app.javashop.framework.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户禁用消息接收器
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019/12/27
 * spring:
 *   application:
 *     name: base-api
 */

//@Component
public class UserDisableReceiver implements RedisMsgReceiver {

    public UserDisableReceiver(List<AuthenticationService> authenticationServices) {
        this.authenticationServices = authenticationServices;
    }

    private List<AuthenticationService> authenticationServices;

    @Override
    public String getChannelName() {
        return RedisChannel.USER_DISABLE;
    }

    @Override
    public void receiveMsg(String message) {
        UserDisableMsg userDisableMsg = JsonUtil.jsonToObject(message,UserDisableMsg.class);
        if (authenticationServices != null) {
            for (AuthenticationService authenticationService : authenticationServices) {
                authenticationService.userDisableEvent(userDisableMsg);
            }
        }

    }
}
