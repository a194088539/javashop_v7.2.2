package com.enation.app.javashop.framework.context.request;

import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;


/**
 * javashop 上下文初始化
 * 以及跨域的支持
 *
 * @author kingapex
 * @version v1.0
 * @since v7.0.0
 * 2018年3月23日 上午10:26:41
 */
public class JavashopRequestInterceptor extends HandlerInterceptorAdapter {

    @Autowired(required = false)
    private JavashopConfig javashopConfig;

    @Value("${spring.cloud.config.profile:dev}")
    private String profile;

    private AntPathMatcher matcher = new AntPathMatcher();

    private static Logger logger = LoggerFactory.getLogger(JavashopRequestInterceptor.class);

    /**
     * 拦截request和response并放到上下文中
     *
     * @param request  要拦截的request
     * @param response 要拦截的response
     * @param handler  spring 机制传递过来的
     * @return 不中断，继续执行，返回为true
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        boolean flag = checkReferer(request, response);

        if (flag) {
            //request 和response存到 上下文中
            ThreadContextHolder.setHttpResponse(response);
            ThreadContextHolder.setHttpRequest(request);

            return super.preHandle(request, response, handler);
        }

        return false;

    }

    @Value("${javashop.referer.checked:false}")
    private boolean checked;

    /**
     * 验证referer
     *
     * @param request
     * @param response
     * @return
     */
    private boolean checkReferer(HttpServletRequest request,
                                 HttpServletResponse response) {
        if (!checked) {
            return true;
        }
        String referer = request.getHeader("referer");
        String host = request.getServerName();
        String uri = request.getRequestURI();

        logger.debug("referer=" + referer);
        logger.debug("uri=" + request.getRequestURI());

        boolean flag = uri.startsWith("/client")
                || "/load-customwords".equals(uri)
                || "/swagger-ui.html".equals(uri)
                || uri.startsWith("/order/pay/weixin/qr")
                || uri.startsWith("/order/pay/weixin/status")
                || uri.startsWith("/payment/callback")
                || uri.startsWith("/payment/return")
                || uri.startsWith("/debugger")
                || uri.contains("/callback")
                || uri.startsWith("/passport/connect");
        if (referer == null && flag) {
            // 状态置为404
            return true;
        } else if (referer == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            logger.debug("本次请求的referer为空");
            return false;
        }

        java.net.URL url = null;
        try {
            url = new java.net.URL(referer);
        } catch (MalformedURLException e) {
            // URL解析异常，也置为404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }
        // 首先判断请求域名和referer域名是否相同
        if (!host.equals(url.getHost())) {
            // 如果不等，判断是否在白名单中
            if (javashopConfig.getReferer() != null) {
                for (String s : javashopConfig.getReferer()) {
                    if (matcher.match(s, url.getHost())) {
                        return true;
                    }
                }
            }
            logger.debug("当前referer没有加入到配置中：" + url.getHost());
            return false;
        }
        return true;
    }

    /**
     * 处理完成 从上下文中移除 request 和respseon
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        ThreadContextHolder.remove();

        super.afterCompletion(request, response, handler, ex);
    }
}
