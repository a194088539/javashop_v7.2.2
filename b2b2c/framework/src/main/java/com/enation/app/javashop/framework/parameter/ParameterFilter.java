package com.enation.app.javashop.framework.parameter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 入参拦截,对request重新包装，目前只处理了emoji表情的拦截
 * @author fk
 * @version v2.0
 * @since v7.2.0
 * 2020.5.8
 */
public class ParameterFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        EmojiEncodeRequestWrapper emojiRequest= new EmojiEncodeRequestWrapper((HttpServletRequest)request);
        chain.doFilter(emojiRequest, response);
    }

    @Override
    public void destroy() {

    }

}
