package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: RecordStoreRule
 * @Author: lanhb
 * @Description: 用来定义平台录音存储规则定义
 * @Date: 2020/4/17 9:41
 * @Version: 1.0
 */
public class RecordStoreRule {

    public String mntDir;  //录音盘阵的挂载目录

    public String grokPattern; //录音文件的完整grok表达式

    public String example; //录音文件在盘阵存储的一个实例

    public String recordIndex;  //该录音文件的录音索引

    public DateFormat dateFormat; //用来定义录音文件存储路径中的时间规则

    public String getMntDir() {
        return mntDir;
    }

    public void setMntDir(String mntDir) {
        this.mntDir = mntDir;
    }

    public String getGrokPattern() {
        return grokPattern;
    }

    public void setGrokPattern(String grokPattern) {
        this.grokPattern = grokPattern;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(String recordIndex) {
        this.recordIndex = recordIndex;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }
}
