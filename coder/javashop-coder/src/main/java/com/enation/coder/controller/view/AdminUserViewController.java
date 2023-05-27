package com.enation.coder.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.enation.coder.service.IAdminUserManager;

/**
 * Created by kingapex on 23/02/2018.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 23/02/2018
 */
@Controller
@RequestMapping("/view/adminuser")
public class AdminUserViewController {

    @Autowired
    private IAdminUserManager adminUserManager;

    @RequestMapping("/list")
    public String list(){

        return "adminuser_list";
    }

    @RequestMapping("/profile")
    public  String profile( ){

        return "profile";
    }



}
