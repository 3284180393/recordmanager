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
    private List<RecordStoreRule> storeRules;

    public List<RecordStoreRule> getStoreRules() {
        return storeRules;
    }

    public void setStoreRules(List<RecordStoreRule> storeRules) {
        this.storeRules = storeRules;
    }
}
