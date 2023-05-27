package com.enation.app.javashop.framework.context.params;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import javax.servlet.ServletRequest;

/**
 * 蛇形转驼峰参数转换器
 * 用于非基本类型
 * Created by kingapex on 2018/3/20.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/20
 */
public class SnakeToCamelModelAttributeMethodProcessor extends ServletModelAttributeMethodProcessor {


    /**
     * 构造函数
     *
     * @param annotationNotRequired 注解是否必须
     */
    public SnakeToCamelModelAttributeMethodProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    /**
     * 绑定蛇形转驼峰Binder
     *
     * @param binder  spring 机制传琛来的binder
     * @param request spring机制的web request
     */
    @Override
    @InitBinder
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest request) {
        SnakeToCamelRequestDataBinder camelBinder = new SnakeToCamelRequestDataBinder(binder.getTarget(), binder.getObjectName());
        ServletRequest req= request.getNativeRequest(ServletRequest.class);
//        Enumeration<String> paramNames = req.getParameterNames();
//
//        while (paramNames.hasMoreElements()){
//            String name = paramNames.nextElement();
//            if (name.contains("[")||name.contains("]")) {
//                return;
//            }
//        }
        camelBinder.bind(req);
        super.bindRequestParameters(binder, request);
    }


}
