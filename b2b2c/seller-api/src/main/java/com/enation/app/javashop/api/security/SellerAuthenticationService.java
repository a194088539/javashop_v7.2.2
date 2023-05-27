package com.enation.app.javashop.api.security;

import com.enation.app.javashop.framework.auth.AuthUser;
import com.enation.app.javashop.framework.security.impl.AbstractAuthenticationService;
import com.enation.app.javashop.framework.security.model.Clerk;
import com.enation.app.javashop.framework.security.model.Role;
import com.enation.app.javashop.framework.security.model.User;
import org.springframework.stereotype.Component;

/**
 * seller 鉴权管理
 * v2.0: 重构鉴权机制
 * Created by zh on 2018/3/12.
 *
 * @author zh
 * @version 2.0
 * @since 7.0.0
 * 2018/3/12
 */
@Component
public class SellerAuthenticationService extends AbstractAuthenticationService {

    /**
     * 将token解析为Clerk
     *
     * @param token
     * @return
     */
    @Override
    protected AuthUser parseToken(String token) {
        AuthUser authUser = tokenManager.parse(Clerk.class, token);
        User user = (User) authUser;
        checkUserDisable(Role.CLERK, user.getUid());
        return authUser;
    }

}
