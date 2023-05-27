package com.enation.framework;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.impl.JdbcDaoSupport;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by kingapex on 2018/3/6.
 *
 * @author kingapex
 * @version 1.0
 * @since 6.4.0
 * 2018/3/6
 */
@Configuration
public class GlobalConfig {


    @Bean(name = "daoSupport")
    @Primary
    public IDaoSupport daoSupport(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        IDaoSupport daosupport = new JdbcDaoSupport(jdbcTemplate);
        return daosupport;
    }
}
