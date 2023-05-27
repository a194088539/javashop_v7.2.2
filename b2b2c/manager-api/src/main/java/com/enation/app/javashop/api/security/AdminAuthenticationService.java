package com.enation.app.javashop.api.security;

import com.enation.app.javashop.framework.auth.AuthUser;
import com.enation.app.javashop.framework.security.impl.AbstractAuthenticationService;
import com.enation.app.javashop.framework.security.model.Admin;
import com.enation.app.javashop.framework.security.model.Role;
import com.enation.app.javashop.framework.security.model.User;
import org.springframework.stereotype.Component;

/**
 * admin 鉴权管理
 * v2.0: 重构鉴权机制
 * Created by kingapex on 2018/3/12.
 *
 * @author kingapex
 * @version 2.0
 * @since 7.0.0
 * 2018/3/12
 */

@Component
public class AdminAuthenticationService extends AbstractAuthenticationService {


    /**
     * 将token解析为Admin
     * @param token
     * @return
     */
    @Override
    protected AuthUser parseToken(String token) {

        AuthUser authUser=  tokenManager.parse(Admin.class, token);
        User user = (User) authUser;
        checkUserDisable(Role.ADMIN, user.getUid());
        return authUser;

    }

}
