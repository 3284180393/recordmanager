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

    private MasterRecord master;

    private BackupRecord backup;

    public MasterRecord getMaster() {
        return master;
    }

    public void setMaster(MasterRecord master) {
        this.master = master;
    }

    public BackupRecord getBackup() {
        return backup;
    }

    public void setBackup(BackupRecord backup) {
        this.backup = backup;
    }
}
