package com.channelsoft.ccod.recordmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName: MixRecordCfg
 * @Author: lanhb
 * @Description: 用来定义混音录音文件配置
 * @Date: 2020/4/1 21:18
 * @Version: 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "record.mix")
public class MixRecordCfg {

    private boolean isBackup;

    private List<DiskScanRole> scanRoles;

    private String example;

    private String recordIndex;

    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
    }

    public List<DiskScanRole> getScanRoles() {
        return scanRoles;
    }

    public void setScanRoles(List<DiskScanRole> scanRoles) {
        this.scanRoles = scanRoles;
    }
}
