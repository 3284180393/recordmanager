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
 * @ClassName: UcdsDataSourceCfg
 * @Author: lanhb
 * @Description: 用来定义ucds配置库jdbcTemplate加载
 * @Date: 2020/4/10 14:23
 * @Version: 1.0
 */
@Conditional(UCDSCondtion.class)
@Configuration
public class UcdsDataSourceCfg {

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
