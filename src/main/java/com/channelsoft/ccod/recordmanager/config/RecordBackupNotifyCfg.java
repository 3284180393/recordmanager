package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: RecordBackupNotifyCfg
 * @Author: lanhb
 * @Description: 配置如何通知录音备份结果的类
 * @Date: 2020/4/6 20:24
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "notify.record-backup")
public class RecordBackupNotifyCfg {

    private DingDing dingding;  //用来定义接受报警消息的钉钉配置

    public DingDing getDingding() {
        return dingding;
    }

    public void setDingding(DingDing dingding) {
        this.dingding = dingding;
    }
}