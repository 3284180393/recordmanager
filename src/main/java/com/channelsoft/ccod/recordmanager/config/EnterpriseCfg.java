package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.EnterpriseChoseMethod;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName: EnterpriseCfg
 * @Author: lanhb
 * @Description: 用来配置需要检查/备份的企业
 * @Date: 2020/4/8 1:34
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "ccod.enterprise")
public class EnterpriseCfg {

    public EnterpriseChoseMethod choseMethod; //企业选择方式

    public List<String> list; //企业列表

    public List<String> notCheckBakList; //忽略备份录音企业列表

    public List<String> ignoreSchemaList; //忽略检查的schema列表，目前只对大域企业有效

    public EnterpriseChoseMethod getChoseMethod() {
        return choseMethod;
    }

    public void setChoseMethod(EnterpriseChoseMethod choseMethod) {
        this.choseMethod = choseMethod;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<String> getNotCheckBakList() {
        return notCheckBakList;
    }

    public void setNotCheckBakList(List<String> notCheckBakList) {
        this.notCheckBakList = notCheckBakList;
    }

    public List<String> getIgnoreSchemaList() {
        return ignoreSchemaList;
    }

    public void setIgnoreSchemaList(List<String> ignoreSchemaList) {
        this.ignoreSchemaList = ignoreSchemaList;
    }
}
