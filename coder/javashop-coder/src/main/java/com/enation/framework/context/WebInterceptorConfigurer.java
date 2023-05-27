package com.enation.framework.context;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * 加载自定义的 拦截器
 * @author Chopper
 * @version v1.0
 * @since v6.2
 * 2017年3月7日 下午5:29:56 
 *
 */
@Configuration
public class WebInterceptorConfigurer implements WebMvcConfigurer {


	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add( new DataTablesPagerArgumentResolver());

	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor( new JavashopRequestInterceptor() ).addPathPatterns("/data/**").addPathPatterns("/view/**").addPathPatterns("/user/**").addPathPatterns("/file/**");
		registry.addInterceptor( new JavashopPermssionInterceptor() ).addPathPatterns("/data/**").addPathPatterns("/view/**").excludePathPatterns("/data/installer/**");
	}
}
