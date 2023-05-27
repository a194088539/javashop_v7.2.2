package com.enation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kingapex on 12/02/2018.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 12/02/2018
 */
@SpringBootApplication
public class JavashopCoderApplication {

    public static void main(String[] args) {

        SpringApplication.run(JavashopCoderApplication.class,args);
    }


//    @Bean
//    public ServletRegistrationBean testServletRegistration() {
//        ServletRegistrationBean registration = new ServletRegistrationBean(new SSIServlet());
//        registration.addUrlMappings("*.shtml");
//        return registration;
//    }


}
