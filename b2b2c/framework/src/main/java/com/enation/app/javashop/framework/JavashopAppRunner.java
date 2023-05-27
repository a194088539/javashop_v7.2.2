package com.enation.app.javashop.framework;

import com.enation.app.javashop.framework.context.instance.InstanceContext;
import com.enation.app.javashop.framework.context.user.UserContext;
import com.enation.app.javashop.framework.context.user.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Javashop项目启动配置
 *
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-05-28
 */

@Component
@Order(value = 1)
public class JavashopAppRunner implements ApplicationRunner {


    /**
     * 用户信息holder，认证信息的获取者
     */
    @Autowired
    private UserHolder userHolder;

    @Autowired
    private JavashopConfig javashopConfig;

    @Autowired
    protected InstanceContext instanceContext;

    /**
     * 在项目加载时指定认证信息获取者
     * 默认是由spring 安全上下文中获取
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (javashopConfig.getTokenSecret() == null) {
            String errorMsg = "配置异常:未配置token秘钥，请检查如下：\n";
            errorMsg += "===========================\n";
            errorMsg += "   javashop.token-secret\n";
            errorMsg += "===========================";

            throw new Exception(errorMsg);
        }

        UserContext.setHolder(userHolder);

        //注册本实例
        instanceContext.register();

    }


}
