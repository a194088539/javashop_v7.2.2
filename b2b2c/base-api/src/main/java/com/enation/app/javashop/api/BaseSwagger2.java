package com.enation.app.javashop.api;

import com.enation.app.javashop.framework.swagger.AbstractSwagger2;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by kingapex on 2018/3/10.
 * Swagger2配置
 *
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/10
 */

@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class BaseSwagger2 extends AbstractSwagger2 {

}
