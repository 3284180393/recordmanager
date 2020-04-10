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
 * @ClassName: BuzOracleDataSourceCfg
 * @Author: lanhb
 * @Description: 用来加载第一个oracle业务库jdbcTemplate
 * @Date: 2020/4/10 12:07
 * @Version: 1.0
 */
@Conditional(BuzOracleCondition.class)
@Configuration
public class BuzOracleDataSourceCfg {

    @Bean(name = "businessDataSource")
    @Qualifier("businessDataSource")
    @ConfigurationProperties(prefix="spring.datasource.business")
    public DataSource businessDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "businessJdbcTemplate")
    @Qualifier("businessJdbcTemplate")
    public JdbcTemplate ucdsJdbcTemplate(
            @Qualifier("businessDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
