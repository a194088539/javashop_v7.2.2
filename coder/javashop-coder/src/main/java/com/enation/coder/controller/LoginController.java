package com.enation.coder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enation.coder.service.IAdminUserManager;
import com.enation.framework.context.ThreadContextHolder;
import com.enation.framework.validator.UnProccessableServiceException;

/**
 * Created by kingapex on 23/02/2018.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 23/02/2018
 */
@RestController
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private IAdminUserManager adminUserManager;

    @PostMapping("/login")
    public  void login(String username,String password,String code){
        String real_code  = (String) ThreadContextHolder.getHttpRequest().getSession().getAttribute(ValidCodeController.SESSION_VALID_CODE);

        if(code!=null && code.equals(real_code)){
            adminUserManager.login(username,password);
        }else {
            throw   new UnProccessableServiceException("验证码错误");
        }

    }

    @PostMapping("/logout")
    public  void logout() {
        adminUserManager.logout();
    }

}
