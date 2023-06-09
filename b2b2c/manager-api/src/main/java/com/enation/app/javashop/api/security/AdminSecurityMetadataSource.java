package com.enation.app.javashop.api.security;

import com.enation.app.javashop.model.base.CachePrefix;
import com.enation.app.javashop.client.system.RoleClient;
import com.enation.app.javashop.framework.cache.Cache;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 权限数据源提供者<br/>
 * 提供此资源需要的角色集合
 * Created by kingapex on 2018/3/27.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/27
 */
public class AdminSecurityMetadataSource implements org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private RoleClient roleClient;

    private Cache cache;

    public AdminSecurityMetadataSource(RoleClient roleClient, Cache cache) {
        this.roleClient = roleClient;
        this.cache = cache;
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;
        String url = fi.getRequestUrl();

        return this.buildAttributes(url);

    }


    private Collection<ConfigAttribute> buildAttributes(String url) {
        List<String> roleList = new ArrayList<>();

        //从缓存中获取各个角色分别对应的菜单权限，缓存中获取不到从数据库获取，放入缓存。
        Map<String, List<String>> roleMap = (Map<String, List<String>>) cache.get(CachePrefix.ADMIN_URL_ROLE.getPrefix());
        if (roleMap == null) {
            roleMap = roleClient.getRoleMap();
        }

        if (roleMap != null) {
            for (String role : roleMap.keySet()) {
                List<String> urlList = roleMap.get(role);
                if (matchUrl(urlList, url)) {
                    roleList.add("ROLE_" + role);
                }
            }
        }

        if (roleList.isEmpty()) {
            //没有匹配到,默认是要超级管理员才能访问
            return SecurityConfig.createList("ROLE_SUPER_ADMIN");

        } else {
            return SecurityConfig.createList(roleList.toArray(new String[roleList.size()]));
        }

    }


    /**
     * 看一个list 中是否匹配某个url
     *
     * @param patternList 一个含有ant表达式的list
     * @param url         要匹配的Url
     * @return 是否有可以匹配此url的表达式, 有返回true
     */
    private boolean matchUrl(List<String> patternList, String url) {
        // 遍历权限
        for (String expression : patternList) {

            // 匹配权限
            boolean isMatch = Pattern.matches(expression, url);

            if (isMatch) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
