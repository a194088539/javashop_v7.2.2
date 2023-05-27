package com.enation.coder.service.impl;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.coder.model.po.AdminUser;
import com.enation.coder.service.IAdminUserManager;
import com.enation.framework.context.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
import com.enation.framework.validator.UnProccessableServiceException;

/**
 * Created by kingapex on 23/02/2018.
 * 管理员管理业务类
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 23/02/2018
 */

@Service
public class AdminUserManagerImpl implements IAdminUserManager {

    @Autowired
    private IDaoSupport daoSupport;

//    @Autowired
//    private ILogManager logManager;

    private static final String CURRENT_ADMINUSER_KEY="CURRENT_ADMINUSER_KEY";
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer add(AdminUser adminUser) {

        adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
        // 添加管理员
        this.daoSupport.insert("es_adminuser", adminUser);

//        Log log = new Log();
//        log.setLog_subject("新建管理员");
//        log.setCustomer_id(0);
//        String detail = "新增管理员："+ adminUser.getRealname() +",角色："+ adminUser.getRole_name();
//        log.setLog_detail(detail);
//
//        logManager.add(log);
        return null;
    }

    @Override
    public int login(String username, String password) {

        password =  StringUtil.md5(password);

        String sql = "select * from es_adminuser where username=?";

        List<AdminUser> userList = this.daoSupport.queryForList(sql, AdminUser.class, username);
        if (userList == null || userList.size() == 0) {
            throw new UnProccessableServiceException( "此用户不存在！");
        }
        AdminUser user = userList.get(0);

        if (!password.equals(user.getPassword())) {
            throw new UnProccessableServiceException( "密码错误！");
        }

//        if (user.getState() == 0) {
//            throw new UnProccessableServiceException("此用户已经被禁用！");
//        }

        HttpSession sessonContext = ThreadContextHolder.getHttpRequest().getSession();
        sessonContext.setAttribute( CURRENT_ADMINUSER_KEY, user);
        return 1;
    }

    @Override
    public void logout() {
        HttpSession sessonContext = ThreadContextHolder.getHttpRequest().getSession();
        sessonContext.removeAttribute( CURRENT_ADMINUSER_KEY);
    }

    @Override
    public AdminUser get(Integer id) {
        return this.daoSupport.queryForObject("select * from es_adminuser where userid=?", AdminUser.class, id);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void edit(int userid,AdminUser adminUser) {
        // 修改用户基本信息
        if (!StringUtil.isEmpty(adminUser.getPassword()))
            adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
        this.daoSupport.update("es_adminuser", adminUser, "userid=" + userid);
//        Log log = new Log();
//        log.setLog_subject("修改管理员");
//        log.setCustomer_id(0);
//        String detail = "修改管理员："+ adminUser.getRealname() +",角色："+ adminUser.getRole_name();
//        log.setLog_detail(detail);
//
//        logManager.add(log);
    }

    @Override
    public void delete(Integer id) {
        // 如果只有一个管理员，则抛出异常
        if (this.checkLast() == 1) {
            throw new UnProccessableServiceException("必须最少保留一个管理员");
        }

        AdminUser adminUser = get(id);

        // 删除用户基本信息
        this.daoSupport.execute("delete from es_adminuser where userid=?", id);

//        Log log = new Log();
//        log.setLog_subject("删除管理员");
//        log.setCustomer_id(0);
//        String detail = "删除管理员："+ adminUser.getRealname() +",角色："+ adminUser.getRole_name();
//        log.setLog_detail(detail);
//        logManager.add(log);

    }

    @Override
    public Page<AdminUser> list(int page_no, int page_size) {
        String  sql="select * from es_adminuser order by userid desc";
        return this.daoSupport.queryForPage(sql,page_no,page_size,AdminUser.class);
    }

    @Override
    public AdminUser getCurrentUser() {
        HttpSession sessonContext = ThreadContextHolder.getHttpRequest().getSession();
        AdminUser adminUser = (AdminUser)sessonContext.getAttribute( CURRENT_ADMINUSER_KEY );
        return adminUser;
    }

    @Override
    public void saveProfile(Profile profile) {

        String password  = "";
        if(StringUtil.notEmpty(password)) {
            AdminUser self = this.getCurrentUser();
            password= StringUtil.md5(password);
            daoSupport.execute("update es_adminuser set password=? where userid=?",password,self.getUserid());

        }
    }

    private int checkLast() {
        int count = this.daoSupport.queryForInt("select count(0) from es_adminuser");
        return count;
    }


}
