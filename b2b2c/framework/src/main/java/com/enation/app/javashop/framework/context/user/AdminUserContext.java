package com.enation.app.javashop.framework.context.user;

import com.enation.app.javashop.framework.security.model.Admin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 用户上下文
 * Created by kingapex on 2018/3/12.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/12
 */
public class AdminUserContext {

    /**
     * 获取当前管理员
     *
     * @return
     */
    public static Admin getAdmin() {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object someOne = authentication.getDetails();
        if (someOne != null && someOne instanceof Admin) {
            return (Admin) someOne;
        }
        return null;


    }


}
