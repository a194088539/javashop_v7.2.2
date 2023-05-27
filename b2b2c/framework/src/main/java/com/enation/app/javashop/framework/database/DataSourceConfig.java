package com.enation.app.javashop.framework.database;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import com.enation.app.javashop.framework.database.impl.DaoSupportImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by kingapex on 2018/3/6.
 * 数据源配置
 * @author kingapex
 * @version 1.0
 * @since 7.0.0
 * 2018/3/6
 */
@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    /*----------------------------------------------------------------------------*/
    /*                           DaoSupport配置                                    */
    /*----------------------------------------------------------------------------*/

    /**
     * 商品daoSupport
     * @param jdbcTemplate 商品jdbcTemplate
     * @return
     */
    @Bean(name = "goodsDaoSupport")
    @Primary
    public DaoSupport goodsDaoSupport(JdbcTemplate jdbcTemplate) {
        DaoSupport daosupport = new DaoSupportImpl(jdbcTemplate);
        return daosupport;
    }

    /**
     * 交易daoSupport
     * @param jdbcTemplate 交易jdbcTemplate
     * @return
     */
    @Bean(name = "tradeDaoSupport")
    public DaoSupport tradeDaoSupport( JdbcTemplate jdbcTemplate) {
        DaoSupport daosupport = new DaoSupportImpl(jdbcTemplate);
        return daosupport;
    }



    /**
     * 会员daoSupport
     * @param jdbcTemplate 会员jdbcTemplate
     * @return
     */
    @Bean(name = "memberDaoSupport")
    public DaoSupport memberDaoSupport(JdbcTemplate jdbcTemplate) {
        DaoSupport daosupport = new DaoSupportImpl(jdbcTemplate);
        return daosupport;
    }

    /**
     * 系统daoSupport
     * @param jdbcTemplate 系统 jdbcTemplate
     * @return
     */
    @Bean(name = "systemDaoSupport")
    public DaoSupport systemDaoSupport(JdbcTemplate jdbcTemplate) {
        DaoSupport daosupport = new DaoSupportImpl(jdbcTemplate);
        return daosupport;
    }


    /**
     * 统计 daoSupport
     * @param jdbcTemplate 统计jdbcTemplate
     * @return
     */
    @Bean(name = "sssDaoSupport")
    public DaoSupport sssDaoSupport(JdbcTemplate jdbcTemplate) {
        DaoSupport daosupport = new DaoSupportImpl(jdbcTemplate);
        return daosupport;
    }


    /**
     * 分销 daoSupport
     * @param jdbcTemplate 分销jdbcTemplate
     * @return
     */
    @Bean(name = "distributionDaoSupport")
    public DaoSupport distributionDaoSupport( JdbcTemplate jdbcTemplate) {
        DaoSupport daosupport = new DaoSupportImpl(jdbcTemplate);
        return daosupport;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    /*----------------------------------------------------------------------------*/
    /*                           事务配置                                         */
    /*----------------------------------------------------------------------------*/

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 会员事务
     * @return
     */
    @Bean
    public PlatformTransactionManager memberTransactionManager(PlatformTransactionManager transactionManager) {
        return transactionManager;
    }

    /**
     * 商品事务
     * @return
     */
    @Bean
    @Primary
    public PlatformTransactionManager goodsTransactionManager(PlatformTransactionManager transactionManager) {
        return transactionManager;
    }

    /**
     * 交易事务
     * @return
     */
    @Bean
    public PlatformTransactionManager tradeTransactionManager(PlatformTransactionManager transactionManager) {
        return transactionManager;
    }

    /**
     * 系统事务
     * @return
     */
    @Bean
    public PlatformTransactionManager systemTransactionManager(PlatformTransactionManager transactionManager) {
        return transactionManager;
    }

    /**
     * 统计事务
     * @return
     */
    @Bean
    public PlatformTransactionManager sssTransactionManager(PlatformTransactionManager transactionManager) {
        return transactionManager;
    }


    /**
     * 分销事务
     * @return
     */
    @Bean
    public PlatformTransactionManager distributionTransactionManager(PlatformTransactionManager transactionManager) {
        return transactionManager;
    }

    /**
     * mybatis plus 分页设置
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置请求的页面大于最大页后操作， true调回到首页，false 继续请求  默认false
        // paginationInterceptor.setOverflow(false);
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        // paginationInterceptor.setLimit(500);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }
}
