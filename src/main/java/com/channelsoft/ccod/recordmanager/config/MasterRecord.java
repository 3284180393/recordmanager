package com.channelsoft.ccod.recordmanager.config;

import java.util.List;

/**
 * @ClassName: MasterRecord
 * @Author: lanhb
 * @Description: 用来定义主录音配置
 * @Date: 2020/4/15 19:10
 * @Version: 1.0
 */
public class MasterRecord {

    private List<RecordStoreRule> storeRules;

    public List<RecordStoreRule> getStoreRules() {
        return storeRules;
    }

    public void setStoreRules(List<RecordStoreRule> storeRules) {
        this.storeRules = storeRules;
    }
}
