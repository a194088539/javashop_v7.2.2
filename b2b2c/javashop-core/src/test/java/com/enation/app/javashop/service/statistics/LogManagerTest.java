package com.enation.app.javashop.service.statistics;

import com.enation.app.javashop.service.system.LogManager;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description : TODO
 * @Author snow
 * @Date: 2020-02-22 11:24
 * @Version v1.0
 */

public class LogManagerTest extends BaseTest {

    @Autowired
    private LogManager logManager;

    @Test
    public void test(){
        String appName = "buyer-api";
        List list = this.logManager.instancesList(appName);

        System.out.println(list.toString());

        try {
            this.logManager.getLogs(appName,list.toArray()[0]+"","2020-02-11",1,100);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
