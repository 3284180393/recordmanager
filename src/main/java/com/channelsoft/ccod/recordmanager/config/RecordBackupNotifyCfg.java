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

    public DingDing dingding;  //用来定义接受报警消息的钉钉配置

    public SysLog sysLog; //用来定义消息是否写到sysLog以及怎么写到sysLog

    public Wechat wechat; //用来定义微信相关配置

    public DingDing getDingding() {
        return dingding;
    }

    public void setDingding(DingDing dingding) {
        this.dingding = dingding;
    }

    public SysLog getSysLog() {
        return sysLog;
    }

    public void setSysLog(SysLog sysLog) {
        this.sysLog = sysLog;
    }

    public Wechat getWechat() {
        return wechat;
    }

    public void setWechat(Wechat wechat) {
        this.wechat = wechat;
    }
}
