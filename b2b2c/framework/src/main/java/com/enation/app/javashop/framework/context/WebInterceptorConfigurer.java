package com.enation.app.javashop.framework.context;

import com.enation.app.javashop.framework.context.params.SnakeToCamelArgumentResolver;
import com.enation.app.javashop.framework.context.params.SnakeToCamelModelAttributeMethodProcessor;
import com.enation.app.javashop.framework.context.request.JavashopRequestInterceptor;
import com.enation.app.javashop.framework.parameter.ParameterFilter;
import com.enation.app.javashop.framework.security.XssStringJsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import java.util.List;

/**
 * 
 * 加载自定义的 拦截器
 * @author Chopper
 * @version v1.0
 * @since v6.2 2017年3月7日 下午5:29:56
 *
 */
@Configuration
public class WebInterceptorConfigurer implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

		//参数蛇形转驼峰拦截
		argumentResolvers.add(new SnakeToCamelModelAttributeMethodProcessor(true));
		argumentResolvers.add(new SnakeToCamelArgumentResolver());

	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(javashopRequestInterceptor()).addPathPatterns("/**");
	}

	/**
	 * 声明javashopRequestInterceptor bean
	 * @return
	 */
	@Bean
	public JavashopRequestInterceptor javashopRequestInterceptor() {
		return new JavashopRequestInterceptor();
	}

	/**
	 * 自定义 ObjectMapper ，用于xss攻击过滤
 	 * @param builder
	 * @return
	 */
	@Bean
	@Primary
	public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
		//解析器
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		//注册xss解析器
		SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
		xssModule.addSerializer(new XssStringJsonSerializer());
		objectMapper.registerModule(xssModule);
		//返回
		return objectMapper;
	}


	/**
	 * 处理参数拦截器声明，目前只处理emoji表情拦截
	 * @return
	 */
	@Bean
	public FilterRegistrationBean<ParameterFilter> parameterFilter() {
		FilterRegistrationBean<ParameterFilter> filter=new FilterRegistrationBean<>();
		filter.setDispatcherTypes(DispatcherType.REQUEST);
		filter.setFilter(new ParameterFilter());
		//拦截所有请求，如果没有设置则默认“/*”
		filter.addUrlPatterns("/*");
		//设置注册的名称，如果没有指定会使用Bean的名称。此name也是过滤器的名称
		filter.setName("parameterFilter");
		//该filter在filterChain中的执行顺序
		filter.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
		return filter;
	}


}
