package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: DBCfg
 * @Author: lanhb
 * @Description: 用来定义ccod平台数据库相关信息
 * @Date: 2021/1/27 20:50
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "db")
public class DBCfg {

    public GlsDbCfg gls;  //gls库相关配置

    public BusinessDBCfg business; //业务库相关配置

    public DBTableCfg table; //数据库相关表名

    public GlsDbCfg getGls() {
        return gls;
    }

    public void setGls(GlsDbCfg gls) {
        this.gls = gls;
    }

    public BusinessDBCfg getBusiness() {
        return business;
    }

    public void setBusiness(BusinessDBCfg business) {
        this.business = business;
    }

    public DBTableCfg getTable() {
        return table;
    }

    public void setTable(DBTableCfg table) {
        this.table = table;
    }
}
