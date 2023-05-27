package com.enation.app.javashop.framework.logs.appender;

import com.enation.app.javashop.framework.util.DateUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description : 生成动态的ES-index
 * @Author snow
 * @Date: 2020-02-19 11:09
 * @Version v1.0
 */

@Component
@ComponentScan("com.internetitem.logback.elasticsearch")
public class IndexNameConfig {

    /**
     * 读取index-name
     * @return
     */
    public static String getDateStr() {
        return DateUtil.toString(new Date(),"yyyy-MM-dd");
    }
}
