/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.enation.app.javashop.framework.transaction;

import io.seata.spring.boot.autoconfigure.StarterConstants;
import io.seata.spring.boot.autoconfigure.properties.SeataProperties;
import io.seata.spring.boot.autoconfigure.util.SpringUtils;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * seata分布式事务自动化配置
 * 拦截的事spring 的transaction注解
 * Created by kingapex on 2020/1/14.
 * @author kingapex
 * @version 1.0
 * @since 7.2.0
 *  2020/1/14.
 */
@ConditionalOnProperty(prefix = StarterConstants.SEATA_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class SeataAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeataAutoConfiguration.class);

    @Autowired
    private SeataProperties seataProperties;


    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    @Bean
    @DependsOn({"springUtils"})
    public SeataTransactionScanner seataTransactionScanner() {
        LOGGER.info("Automatically configure Seata by spring transaction...");
        return new SeataTransactionScanner(seataProperties.getApplicationId(), seataProperties.getTxServiceGroup());
    }


}
