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
 * @ClassName: UcdsAgentDataSourceCfg
 * @Author: lanhb
 * @Description: 用来定义ucdsAgent的查询数据源
 * @Date: 2021/1/29 18:42
 * @Version: 1.0
 */
@Conditional(UcdsAgentCondition.class)
@Configuration
public class UcdsAgentDataSourceCfg {
    @Bean(name = "ucdsDataSource")
    @Qualifier("ucdsDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.ucds")
    public DataSource ucdsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "ucdsJdbcTemplate")
    public JdbcTemplate ucdsJdbcTemplate(
            @Qualifier("ucdsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
