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
 * @ClassName: MongoBuzDataSourceCfg
 * @Author: lanhb
 * @Description: 用来自动加载mongo业务库的jdbcTemplate
 * @Date: 2020/4/10 10:50
 * @Version: 1.0
 */
@Conditional(MongoBuzCondition.class)
@Configuration
public class MongoBuzDataSourceCfg {

    @Bean(name = "businessDataSource")
    @Qualifier("businessDataSource")
    @ConfigurationProperties(prefix="spring.datasource.business")
    public DataSource businessDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "businessJdbcTemplate")
    public JdbcTemplate ucdsJdbcTemplate(
            @Qualifier("businessDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
