package com.channelsoft.ccod.recordmanager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @ClassName: BigEntDataSourceCfg
 * @Author: lanhb
 * @Description: 用来定义大域企业数据库加载类
 * @Date: 2020/4/7 15:13
 * @Version: 1.0
 */
@Conditional(BigEntPlatformCondition.class)
@Configuration
public class BigEntDataSourceCfg {

    @Bean(name = "glsDataSource")
    @Qualifier("glsDataSource")
    @ConfigurationProperties(prefix="spring.datasource.gls")
    public DataSource glsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "businessDataSource")
    @Qualifier("businessDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.business")
    public DataSource businessDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "glsJdbcTemplate")
    public JdbcTemplate glsJdbcTemplate(
            @Qualifier("glsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "businessDataSource")
    public JdbcTemplate businessJdbcTemplate(
            @Qualifier("businessDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
