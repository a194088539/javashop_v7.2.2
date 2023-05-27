package com.enation.app.javashop.framework.validation.impl;

import com.enation.app.javashop.framework.JavashopConfig;
import com.enation.app.javashop.framework.exception.ServiceException;
import com.enation.app.javashop.framework.exception.SystemErrorCodeV1;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.framework.validation.annotation.DemoSiteDisable;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 演示站点是否可以被操作切面类
 *
 * @author zh
 * @version v7.0
 * @date 19/2/25 上午8:58
 * @since v7.0
 */
@Component
@Aspect
public class DemoSiteAspect {

    @Autowired
    private JavashopConfig javashopConfig;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("@annotation(demoSiteDisable)")
    public void doAfter(DemoSiteDisable demoSiteDisable) throws Exception {
        if (javashopConfig.getIsDemoSite()) {
            logger.debug("演示站点禁止此操作");
            throw new ServiceException(SystemErrorCodeV1.NO_PERMISSION, "演示站点禁止此操作");
        }
    }

}
