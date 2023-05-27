package com.enation.app.javashop.consumer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by kingapex on 2018/3/10.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 2018/3/10
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "com.enation.app.javashop",
        excludeFilters  = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern="com.enation.app.javashop.mapper.*"))
@EnableScheduling
@MapperScan(basePackages = "com.enation.app.javashop.mapper")
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

}
