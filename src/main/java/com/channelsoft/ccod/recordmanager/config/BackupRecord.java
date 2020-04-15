package com.channelsoft.ccod.recordmanager.config;

import java.util.List;

/**
 * @ClassName: BackupRecord
 * @Author: lanhb
 * @Description: 用来定义备份录音配置
 * @Date: 2020/4/15 19:12
 * @Version: 1.0
 */
public class BackupRecord
{
    private List<RecordStoreRole> storeRoles;

    public List<RecordStoreRole> getStoreRoles() {
        return storeRoles;
    }

    public void setStoreRoles(List<RecordStoreRole> storeRoles) {
        this.storeRoles = storeRoles;
    }
}
