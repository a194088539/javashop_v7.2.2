package com.enation.framework.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

 

/**
 *  用ThreadLocal来存储Session,以便实现Session any where 
 * @author kingapex
 * <p>2009-12-17 下午03:10:09</p>
 * @version 1.1
 * 新增request any where
 */
public class ThreadContextHolder  {

	private static ThreadLocal<HttpServletRequest> HttpRequestThreadLocalHolder = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> HttpResponseThreadLocalHolder = new ThreadLocal<HttpServletResponse>();
	
	
	public static void setHttpRequest(HttpServletRequest request){
		
		HttpRequestThreadLocalHolder.set(request);
	}
	
	public static HttpServletRequest getHttpRequest(){
		return  HttpRequestThreadLocalHolder.get();
	}
	
	
	
	public static void remove(){
		HttpRequestThreadLocalHolder.remove();
		HttpResponseThreadLocalHolder.remove();
	}
	
	
	public static void setHttpResponse(HttpServletResponse response){
		HttpResponseThreadLocalHolder.set(response);	
	}
	
	public static HttpServletResponse getHttpResponse(){
		
		return HttpResponseThreadLocalHolder.get();
	}
	

	
}
