package com.enation.app.javashop.api;

import com.enation.app.javashop.model.base.DomainHelper;
import com.enation.app.javashop.framework.context.ApplicationContextHolder;
import com.enation.app.javashop.framework.security.AuthenticationService;
import com.enation.app.javashop.framework.security.TokenAuthenticationFilter;
import com.enation.app.javashop.framework.security.message.UserDisableReceiver;
import com.enation.app.javashop.framework.security.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.AllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.StaticAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by kingapex on 2018/3/12.
 * 买家安全配置
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/12
 */
@Configuration
@EnableWebSecurity
public class BuyerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DomainHelper domainHelper;

    @Autowired
    private BuyerAuthenticationService buyerAuthenticationService;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;




    /**
     * 定义seller工程的权限
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource((CorsConfigurationSource) ApplicationContextHolder.getBean("corsConfigurationSource")).and().csrf().disable()
                //禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                //定义验权失败返回格式
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint).and()
                .authorizeRequests()
                .and()
                .addFilterBefore(new TokenAuthenticationFilter(buyerAuthenticationService),
                        UsernamePasswordAuthenticationFilter.class);

        //过滤掉swagger的路径
        http.authorizeRequests().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**").anonymous();
        //过滤掉不需要买家权限的api
        http.authorizeRequests().antMatchers("/actuator/**","/debugger/**", "/jquery.min.js", "/payment/weixin/**", "/payment/callback/**", "/payment/order/pay/query/**", "/pintuan/orders/**", "/pintuan/goods", "/pintuan/goods/**", "/goods/**", "/pages/**", "/focus-pictures/**",
                "/shops/list", "/shops/{spring:[0-9]+}", "/shops/cats/{spring:[0-9]+}", "/shops/navigations/{spring:[0-9]+}", "/promotions/**", "/view",
                "/shops/sildes/{spring:[0-9]+}", "/members/logout*", "/passport/**", "/trade/goods/**", "/payment/return/**",
                "/members/asks/goods/{spring:[0-9]+}", "/members/asks/detail/{spring:[0-9]+}", "/members/asks/relation/{spring:[0-9]+}/{spring:[0-9]+}", "/members/asks/reply/list/{spring:[0-9]+}", "/members/comments/goods/{spring:[0-9]+}", "/members/comments/goods/{spring:[0-9]+}/count", "/distribution/su/**", "/passport/connect/pc/WECHAT/**", "/passport/login-binder/pc/**", "/account-binder/**", "/wechat/**", "/members/history").permitAll().and();
        //定义有买家权限才可以访问
        http.authorizeRequests().anyRequest().hasRole(Role.BUYER.name());
        http.headers().addHeaderWriter(xFrameOptionsHeaderWriter());
        //禁用缓存
        http.headers().cacheControl().and()
                .contentSecurityPolicy("script-src  'self' 'unsafe-inline' ; frame-ancestors " + domainHelper.getBuyerDomain());

    }


    public XFrameOptionsHeaderWriter xFrameOptionsHeaderWriter() throws URISyntaxException {

        String buyerDomain = domainHelper.getBuyerDomain();

        URI uri = new URI(buyerDomain);

        AllowFromStrategy allowFromStrategy = new StaticAllowFromStrategy(uri);

        XFrameOptionsHeaderWriter xFrameOptionsHeaderWriter = new XFrameOptionsHeaderWriter(allowFromStrategy);

        return xFrameOptionsHeaderWriter;
    }

    /**
     * 定义跨域配置
     *
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Autowired
    private List<AuthenticationService> authenticationServices;


    @Bean
    public UserDisableReceiver userDisableReceiver() {
        return new UserDisableReceiver(authenticationServices);
    }

}
