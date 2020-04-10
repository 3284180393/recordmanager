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
 * @ClassName: Buz2OracleDataSourceCfg
 * @Author: lanhb
 * @Description: 用来自动加载第2个oracle业务库的jdbcTemplate
 * @Date: 2020/4/10 11:56
 * @Version: 1.0
 */
@Conditional(Buz2OracleCondition.class)
@Configuration
public class Buz2OracleDataSourceCfg {

    @Bean(name = "business2DataSource")
    @Qualifier("business2DataSource")
    @ConfigurationProperties(prefix="spring.datasource.business2")
    public DataSource business2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "business2JdbcTemplate")
    @Qualifier("business2JdbcTemplate")
    public JdbcTemplate business2JdbcTemplate(
            @Qualifier("business2DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
