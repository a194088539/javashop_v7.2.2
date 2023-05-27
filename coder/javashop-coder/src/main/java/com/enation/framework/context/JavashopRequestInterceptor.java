package com.enation.framework.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * javashop 上下文初始化
 */
public class JavashopRequestInterceptor extends HandlerInterceptorAdapter {


	@Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
		ThreadContextHolder.setHttpResponse(response);
		ThreadContextHolder.setHttpRequest(request);

		return super.preHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
		ThreadContextHolder.remove();
		super.afterCompletion(request, response, handler, ex);
	}
}
