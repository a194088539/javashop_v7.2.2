package com.enation.app.javashop.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by kingapex on 2018/3/21.
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/21
 */
@SpringBootApplication
@EnableScheduling
public class FrameworkApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrameworkApplication.class, args);
    }

}
