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
 * @ClassName: DataSourceCfg
 * @Author: lanhb
 * @Description: 用来定义数据库加载的类
 * @Date: 2020/4/3 18:19
 * @Version: 1.0
 */
@Conditional(NormalPlatformCondition.class)
@Configuration
public class DataSourceCfg {
    @Bean(name = "glsDataSource")
    @Qualifier("glsDataSource")
    @ConfigurationProperties(prefix="spring.datasource.gls")
    public DataSource glsDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "ucdsDataSource")
    @Qualifier("ucdsDataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.ucds")
    public DataSource ucdsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "glsJdbcTemplate")
    public JdbcTemplate glsJdbcTemplate(
            @Qualifier("glsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    @Bean(name = "ucdsJdbcTemplate")
    public JdbcTemplate ucdsJdbcTemplate(
            @Qualifier("ucdsDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
