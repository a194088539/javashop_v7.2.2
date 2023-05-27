package com.enation.coder.service;


import org.springframework.context.annotation.Profile;

import com.enation.coder.model.po.AdminUser;
import com.enation.framework.database.Page;

/**
 * 管理员管理接口
 * @author kingapex
 * 2010-11-7下午05:49:12
 */
public interface IAdminUserManager {
	


	/**
	 * 为当前站点添加一个管理员
	 * @param adminUser
	 * @return 添加的管理员id
	 */
	Integer add(AdminUser adminUser);
	




	/**
	 * 管理员
	 * @param username
	 * @param password 此处为未经过md5加密的密码
	 * @return 返回登录成功的用户id
	 * @throws RuntimeException 登录失败抛出此异常，登录失败原因可通过getMessage()方法获取
	 */
	  int  login(String username, String password);

	/**
	 * 登出当前会话
	 */
	void logout();



	/**
	 * 读取管理员信息
	 * @param id
	 * @return
	 */
	  AdminUser get(Integer id);


	/**
	 * 修改管理员信息
	 * @param userid 要修改的管理员id
	 * @param user 要修改的管理员信息
	 */
	void edit(int userid,AdminUser user);


	/**
	 * 删除管理员
	 * @param id
	 * @throws RuntimeException  当删除最后一个管理员时
	 */
	 void delete(Integer id);



	/**
	 * 读取此站点所有管理员
	 * @return
	 */
	  Page<AdminUser> list(int page_no,int page_size) ;


	/**
	 * 获取当前登陆的管理员
	 * @return  当前登陆的管理员
	 */
	AdminUser getCurrentUser();

	/**
	 * 保存配置文件
	 * @param profile 配置文件
	 */
	void saveProfile(Profile profile);

}
