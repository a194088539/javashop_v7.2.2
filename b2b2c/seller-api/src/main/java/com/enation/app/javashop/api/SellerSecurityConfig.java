package com.enation.app.javashop.api;

import com.enation.app.javashop.client.member.ShopRoleClient;
import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.security.AuthenticationService;
import com.enation.app.javashop.framework.security.TokenAuthenticationFilter;
import com.enation.app.javashop.framework.security.message.UserDisableReceiver;
import com.enation.app.javashop.api.security.SellerAccessDecisionManager;
import com.enation.app.javashop.api.security.SellerAuthenticationService;
import com.enation.app.javashop.api.security.SellerSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Created by kingapex on 2018/3/12.
 * 卖家安全配置
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/12
 */
@Configuration
@EnableWebSecurity
public class SellerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private ShopRoleClient shopRoleClient;
    @Autowired
    private Cache cache;
    @Autowired
    private SellerAuthenticationService sellerAuthenticationService;


    /**
     * 定义seller工程的权限
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                //定义验权失败返回格式
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint).and()
                .authorizeRequests().anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(
                            O fsi) {
                        fsi.setSecurityMetadataSource(new SellerSecurityMetadataSource(shopRoleClient,cache));
                        fsi.setAccessDecisionManager(new SellerAccessDecisionManager());
                        return fsi;
                    }
                }).and().addFilterBefore(new TokenAuthenticationFilter(sellerAuthenticationService), UsernamePasswordAuthenticationFilter.class);
        //禁用缓存
        http.headers().cacheControl()
                .and().contentSecurityPolicy("script-src 'self'");


    }


    /**
     * 定义跨域配置
     * @return
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addAllowedMethod(CorsConfiguration.ALL);
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
