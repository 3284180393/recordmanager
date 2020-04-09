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
 * @ClassName: Business2DataSourceCfg
 * @Author: lanhb
 * @Description: 用来定义第二个业务库数据源
 * @Date: 2020/4/9 15:56
 * @Version: 1.0
 */
@Conditional(BigEnt2DBPlatformCondition.class)
@Configuration
public class Business2DataSourceCfg {

    @Bean(name = "business2DataSource")
    @Qualifier("business2DataSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.business2")
    public DataSource businessDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "business2JdbcTemplate")
    public JdbcTemplate businessTemplate(
            @Qualifier("business2DataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
