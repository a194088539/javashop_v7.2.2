package com.enation.coder.controller.data;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enation.coder.model.po.AdminUser;
import com.enation.coder.service.IAdminUserManager;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * Created by kingapex on 23/02/2018.
 * 管理员数据管理控制器
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 23/02/2018
 */
@RestController
@RequestMapping("/data/")
@Validated
public class AdminUserDataController {

    @Autowired
    private IAdminUserManager adminUserManager;


    /**
     * 添加一个管理员
     * @param adminUser 管理员
     * @return
     */
    @PostMapping("/adminuser")
    public AdminUser add(@Valid AdminUser adminUser){
        if(StringUtil.isEmpty(adminUser.getPassword())){
            throw new IllegalArgumentException("请输入密码");
        }
        adminUserManager.add(adminUser);
        return adminUser;
    }


    /**
     * 修改一个管理员
     * @param user_id 要修改的用户的id
     * @param adminUser 要修改的信息
     * @return
     */
    @PostMapping("/adminuser/{user_id}")
    public AdminUser edit(@PathVariable("user_id") int user_id,@Valid AdminUser adminUser){
        adminUserManager.edit(user_id,adminUser);

        return  adminUser;
    }

    @GetMapping("/adminuser/{user_id}")
    public AdminUser get(@PathVariable("user_id") int user_id){
        return adminUserManager.get(user_id);
    }

    /**
     * 删除一个管理员
     * @param user_id 要删除的管理员id
     */
    @DeleteMapping("/adminuser/{user_id}")
    public void delete(@PathVariable("user_id") int user_id){
        adminUserManager.delete(user_id);
    }

    /**
     * 读取所有管理员列表
     * @return
     */
    @GetMapping("/adminusers")
    public Page<AdminUser> list(int page_no, int page_size){
        return  adminUserManager.list(page_no,page_size);
    }



    /**
     * 修改当前会话的配置文件
     * @param profile
     * @return
     */
    @PostMapping("/adminuser/profile")
    public Profile profile(  Profile profile){
        adminUserManager.saveProfile(profile);
        return  profile;
    }

}
