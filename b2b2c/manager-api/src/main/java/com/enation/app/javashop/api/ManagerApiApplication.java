package com.enation.app.javashop.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by kingapex on 2018/3/8.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 2018/3/8
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.enation.app.javashop",
        excludeFilters  = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern="com.enation.app.javashop.mapper.*"))
@EnableTransactionManagement
@ServletComponentScan
@EnableScheduling
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class ManagerApiApplication {
    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        SpringApplication.run(ManagerApiApplication.class, args);
    }

}
