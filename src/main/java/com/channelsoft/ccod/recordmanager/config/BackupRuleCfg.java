package com.channelsoft.ccod.recordmanager.config;

import com.channelsoft.ccod.recordmanager.constant.BackupMethod;
import com.channelsoft.ccod.recordmanager.constant.RecordType;

import java.util.List;

/**
 * @ClassName: BackupRuleCfg
 * @Author: lanhb
 * @Description: 用来定义录音备份规则
 * @Date: 2020/4/3 10:10
 * @Version: 1.0
 */
public class BackupRuleCfg {

    private DiskScanRole scanRole;

    private DateFormat dateFormat;

    private RecordType recordType;

    private BackupMethod backupMethod;

    private String storeDir;

    private List<String> excludeEntIds;

    public DiskScanRole getScanRole() {
        return scanRole;
    }

    public void setScanRole(DiskScanRole scanRole) {
        this.scanRole = scanRole;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public BackupMethod getBackupMethod() {
        return backupMethod;
    }

    public void setBackupMethod(BackupMethod backupMethod) {
        this.backupMethod = backupMethod;
    }

    public String getStoreDir() {
        return storeDir;
    }

    public void setStoreDir(String storeDir) {
        this.storeDir = storeDir;
    }

    public List<String> getExcludeEntIds() {
        return excludeEntIds;
    }

    public void setExcludeEntIds(List<String> excludeEntIds) {
        this.excludeEntIds = excludeEntIds;
    }
}
