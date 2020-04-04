package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: DBConstructCfg
 * @Author: lanhb
 * @Description: 用来定义数据库结构的配置类
 * @Date: 2020/4/4 12:47
 * @Version: 1.0
 */
public class DBConstructCfg {

    private String detailTable;

    private String mixRecordTable;

    private String combinationRecordTable;

    private String bakRecordTable;

    public String getDetailTable() {
        return detailTable;
    }

    public void setDetailTable(String detailTable) {
        this.detailTable = detailTable;
    }

    public String getMixRecordTable() {
        return mixRecordTable;
    }

    public void setMixRecordTable(String mixRecordTable) {
        this.mixRecordTable = mixRecordTable;
    }

    public String getCombinationRecordTable() {
        return combinationRecordTable;
    }

    public void setCombinationRecordTable(String combinationRecordTable) {
        this.combinationRecordTable = combinationRecordTable;
    }

    public String getBakRecordTable() {
        return bakRecordTable;
    }

    public void setBakRecordTable(String bakRecordTable) {
        this.bakRecordTable = bakRecordTable;
    }
}
