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
 * @ClassName: BigEnt2DBDataSourceCfg
 * @Author: lanhb
 * @Description: 用来定义2个业务库的大域平台数据库
 * @Date: 2020/4/3 23:18
 * @Version: 1.0
 */
@Conditional(BigEntPlatformCondition.class)
@Configuration
public class BigEnt2DBDataSourceCfg {

    @Bean(name = "glsDataSource")
    @Qualifier("glsDataSource")
    @ConfigurationProperties(prefix="spring.datasource.gls")
    public DataSource glsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "glsJdbcTemplate")
    public JdbcTemplate glsJdbcTemplate(
            @Qualifier("glsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
