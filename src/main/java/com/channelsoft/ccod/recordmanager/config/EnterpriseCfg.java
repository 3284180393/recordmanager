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

    private EnterpriseChoseMethod choseMethod; //企业选择方式

    private List<String> list; //企业列表

    private List<String> notCheckBakList; //忽略备份录音企业列表

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
}
