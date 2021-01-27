package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: RecordCheckNotifyCfg
 * @Author: lanhb
 * @Description: 配置如何通知录音检查结果的类
 * @Date: 2020/4/6 18:06
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "notify.record-check")
public class RecordCheckNotifyCfg {

    public int indexLostCount;  //当录音索引丢失数超过此值将会报警

    public int indexLostRate; //当录音索引丢失率超过此值将会报警

    public int fileLostCount;  //当录音文件丢失数超过此值将会报警

    public int fileLostRate; //当录音文件丢失率超过此值将会报警

    public int bakIndexLostCount; //当备份录音索引丢失数超过此值将会报警

    public int bakIndexLostRate; //当备份录音索引丢失率超过此值将会报警

    public int bakFileLostCount; //当备份录音文件丢失数超过此值将会报警

    public int bakFileLostRate; //当备份录音文件丢失率超过此值将会报警

    public boolean reportNormalResult; //是否报告正常检查结果

    public DingDing dingding;  //用来定义接受报警消息的钉钉配置

    public SysLog sysLog; //用来定义写sysLog的配置

    public Wechat wechat; //用来定义微信相关配置

    public int getIndexLostCount() {
        return indexLostCount;
    }

    public void setIndexLostCount(int indexLostCount) {
        this.indexLostCount = indexLostCount;
    }

    public int getIndexLostRate() {
        return indexLostRate;
    }

    public void setIndexLostRate(int indexLostRate) {
        this.indexLostRate = indexLostRate;
    }

    public int getFileLostCount() {
        return fileLostCount;
    }

    public void setFileLostCount(int fileLostCount) {
        this.fileLostCount = fileLostCount;
    }

    public int getFileLostRate() {
        return fileLostRate;
    }

    public void setFileLostRate(int fileLostRate) {
        this.fileLostRate = fileLostRate;
    }

    public int getBakIndexLostCount() {
        return bakIndexLostCount;
    }

    public void setBakIndexLostCount(int bakIndexLostCount) {
        this.bakIndexLostCount = bakIndexLostCount;
    }

    public int getBakIndexLostRate() {
        return bakIndexLostRate;
    }

    public void setBakIndexLostRate(int bakIndexLostRate) {
        this.bakIndexLostRate = bakIndexLostRate;
    }

    public int getBakFileLostCount() {
        return bakFileLostCount;
    }

    public void setBakFileLostCount(int bakFileLostCount) {
        this.bakFileLostCount = bakFileLostCount;
    }

    public int getBakFileLostRate() {
        return bakFileLostRate;
    }

    public void setBakFileLostRate(int bakFileLostRate) {
        this.bakFileLostRate = bakFileLostRate;
    }

    public boolean isReportNormalResult() {
        return reportNormalResult;
    }

    public void setReportNormalResult(boolean reportNormalResult) {
        this.reportNormalResult = reportNormalResult;
    }

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
