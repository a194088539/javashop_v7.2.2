package com.enation.app.javashop.framework.util;

import com.enation.app.javashop.framework.context.request.ThreadContextHolder;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：liuyulei
 * @date ：Created in 2019/9/22 10:59
 * @description：获取request的客户端ip地址工具
 * @version: v1.0
 * @since: v7.1.4
 */
public class IPUtil {

    /**
     * 获取request的客户端IP地址
     *
     * @param request
     * @return
     */
    public static String getIpAdrress() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtil.isEmpty(ip)) {
            return request.getRemoteAddr();
        }
        if (!StringUtil.isEmpty(ip) && ip.contains(",")) {
            ip = ip.split(",")[0];
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Cdn-Src-Ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
