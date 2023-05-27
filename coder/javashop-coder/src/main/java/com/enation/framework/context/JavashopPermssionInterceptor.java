package com.enation.framework.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.enation.coder.model.enums.Role;
import com.enation.coder.model.po.AdminUser;
import com.enation.framework.validator.NoPermissionException;

/**
 * javashop 上下文初始化
 */
public class JavashopPermssionInterceptor extends HandlerInterceptorAdapter {



	@Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

		init();

		String url  = request.getServletPath();

		AdminUser adminUser = this.getCurrentUser(request);
		
		if(url.startsWith("/view/template")){
			return super.preHandle(request, response, handler);
		}

		if(adminUser==null){
			if(url.startsWith("/view")){
				response.sendRedirect("/index.html");
				return false;
			}
			else {
				throw new NoPermissionException("not_login","无权访问");
			}
		}else {

			//管理员干啥都行
			if(adminUser.getRole().equals(Role.administrator.name())){
				return super.preHandle(request, response, handler);
			}

			String method   = request.getMethod();
			String rolename  = adminUser.getRole();
			Set<Request> requests = disablePermssions.keySet();
			for (Request req:requests){
				//url在禁用范围内
 				if( checkUrl(url,req.url) ){

 					//校验method
 					if(req.method.equals("any") || req.method.equals( method.toUpperCase() )){
						Roles roles = disablePermssions.get(req);
						//有禁用的角色，则没权限
						boolean noPermssion  = roles.checkRole(rolename);
						if(noPermssion){
							throw new NoPermissionException("无权访问");
						}
					}

				}

			}
		}

		return super.preHandle(request, response, handler);
	}

	//private static Map<Request,Roles> enablePermssions = new HashMap();
	private static Map<Request,Roles> disablePermssions =null;


	private  boolean checkUrl(String url,String patternStr){
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(url);
		return  matcher.matches();
	}

	public static void main(String[] args) {
		String patternStr ="/customer/(\\d+)/payment";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher("/data/adminuser/1");
		boolean reslut = matcher.matches();
		System.out.println(reslut);
		System.out.println(matcher.find());
//		System.out.println(matcher.group() );
	}

	private void init() {

		if(disablePermssions!=null )
		{return;}
		disablePermssions = new HashMap();
		
		//禁用管理员添加
		disablePermssions.put(new Request("/data/adminuser","POST"),new Roles().add(Role.commonuser));

		//禁止除管理员个的操作管理员
		disablePermssions.put(new Request("/data/adminuser/.*","any"),new Roles().add(Role.commonuser));

	}


	/**
	 * 角色合集
	 */
	class Roles {
		List<Role> roles;
		Roles(){
			roles = new ArrayList<>();
		}

		Roles add (Role role){

			this.roles.add(role);
			return  this;
		}


		/**
		 * 检测是否含有某个角色
		 * @param rolename 要检测的角色名
		 * @return 是否有此角色
		 */
		boolean checkRole(String  rolename){
//			for( Role role :roles){
//				if(role.name().equals( rolename )){
					return true;
//				}
//			}
//
//			return false;
		}

	}


	/**
	 * 一个请求的封装
	 */
	class Request {

		public String url;
		public String method;

		Request(String _url,String _method){
			this.url = _url;
			this.method = _method;
		}


	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
		if(modelAndView!=null) {
			AdminUser adminUser = this.getCurrentUser(request);
			modelAndView.addObject("user", adminUser);
		}
		super.postHandle(request, response, handler, modelAndView);
	}


	/**
	 * 获取当前登录的会话
	 * @param request
	 * @return
	 */
	private AdminUser getCurrentUser(HttpServletRequest  request){
		AdminUser adminUser = (AdminUser)request.getSession().getAttribute("CURRENT_ADMINUSER_KEY");
		return adminUser;
	}
}
