package com.enation.app.javashop.framework.database;

import com.enation.app.javashop.framework.FrameworkApplication;
import com.enation.app.javashop.framework.cache.Cache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by kingapex on 2018/10/17.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/10/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FrameworkApplication.class)
@ComponentScan("com.enation.app.javashop")
public class RedisTest {
    @Autowired
    private Cache<String> cache;

    @Test
    public void test(){
        cache.put("thekey","thevalue");
        String value  = cache.get("thekey");
        System.out.println("get value "+value);
    }
}
