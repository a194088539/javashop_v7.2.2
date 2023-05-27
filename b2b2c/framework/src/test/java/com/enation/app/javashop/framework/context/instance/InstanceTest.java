package com.enation.app.javashop.framework.context.instance;

import com.enation.app.javashop.framework.cache.Cache;
import com.enation.app.javashop.framework.test.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static jodd.util.ThreadUtil.sleep;

/**
 * 实例相关测试
 * 运行本单元测试需要注释掉：
 * com.enation.app.javashop.framework.context.instance.InstanceContext#register()
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/15
 */

public class InstanceTest extends BaseTest {

    @Autowired
    InstanceContext instanceContext;
    public static String REDIS_KEY = "{instances}";

    @Autowired
    private Cache redisCache;

    @Before
    public void setup() {
        //先清掉缓存并形成6个实例
        redisCache.remove(REDIS_KEY);
        int j=0;
        for (int i = 0; i <=5; i++) {
            AppInstance instance = AppInstance.getInstance();
            instance.setUuid("uuid-" + i);
            //注册一个实例
            instanceContext.register();
            //查看本实例workid
            System.out.println(instance.getWorkId());
        }


    }

    @Test
    public void getAppTest() {
        List<AppInstance> list = instanceContext.getInstances();
        int j=0;
        int i=0;
        for (AppInstance instance : list) {
            if (i % 2 == 0) {
                j++;
            }
            i++;

            instance.setAppName("app"+j);

        }

        redisCache.put(REDIS_KEY,list);
        System.out.println(list);

        Set set = instanceContext.getApps();

        System.out.println(set);
    }
    @Test
    public void getTest() {
        List<AppInstance> list = instanceContext.getInstances();
        System.out.println(list);
    }
    @Test
    public void registerTest() {

        List<AppInstance> list = instanceContext.getInstances();
        System.out.println(list);


        //使一个app失效
        list.remove(2);
        redisCache.put(REDIS_KEY,list);

        //注册一个实例
        instanceContext.register();

        //查看本实例workid
        AppInstance instance = AppInstance.getInstance();
        System.out.println(instance.getWorkId());

        list = instanceContext.getInstances();
        System.out.println(list);
    }


    @Test
    public void heartbeatTest() {
        //注册一个实例
        instanceContext.register();
        List<AppInstance> list = instanceContext.getInstances();
        System.out.println(list);

        sleep(120*1000);

        list = instanceContext.getInstances();
        System.out.println(list);

    }
}
