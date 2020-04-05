package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName: RecordStoreCfg
 * @Author: lanhb
 * @Description: 用来定义录音存储配置
 * @Date: 2020/4/4 22:43
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "record")
public class RecordStoreCfg {

    private List<RecordStoreRole> storeRoles;

    public List<RecordStoreRole> getStoreRoles() {
        return storeRoles;
    }

    public void setStoreRoles(List<RecordStoreRole> storeRoles) {
        this.storeRoles = storeRoles;
    }
}
