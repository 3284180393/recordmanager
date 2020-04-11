package com.channelsoft.ccod.recordmanager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @ClassName: SqliteDataSourceCfg
 * @Author: lanhb
 * @Description: 用来加载自带的sqlite数据库
 * @Date: 2020/4/11 18:24
 * @Version: 1.0
 */
@Configuration
public class SqliteDataSourceCfg {

    @Bean(name = "SQLiteDataSource")
    @Qualifier("SQLiteDataSource")
    @ConfigurationProperties(prefix="spring.datasource.sqlite")
    public DataSource SQLiteDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sqliteJdbcTemplate")
    public JdbcTemplate sqliteJdbcTemplate(@Qualifier("SQLiteDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
