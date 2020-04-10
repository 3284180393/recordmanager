package com.channelsoft.ccod.recordmanager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @ClassName: GlsDataSourceCfg
 * @Author: lanhb
 * @Description: 用来自动加载gls配置库的jdbcTemplate
 * @Date: 2020/4/10 14:49
 * @Version: 1.0
 */
@Conditional(GLSCondition.class)
@Configuration
public class GlsDataSourceCfg {

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
