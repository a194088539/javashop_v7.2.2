package com.enation.app.javashop.model.trade.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author ：liuyulei
 * @date ：Created in 2019/9/11 18:10
 * @description：配置文件启动解析
 * @version: v1.0
 * @since: v7.1.4
 */
@Component
@Order(value = 2)
public class ProfileParseRunner implements ApplicationRunner {

    @Autowired
    private ProfileParse profileParse;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        profileParse.parseProfile();
    }
}
