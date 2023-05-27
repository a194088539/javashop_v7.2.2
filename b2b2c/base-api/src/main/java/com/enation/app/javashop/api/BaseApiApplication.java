package com.enation.app.javashop.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by kingapex on 2018/3/18.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/18
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.enation.app.javashop",
        excludeFilters  = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern="com.enation.app.javashop.mapper.*"))
@EnableScheduling
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class BaseApiApplication {

    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(BaseApiApplication.class, args);
    }
}
