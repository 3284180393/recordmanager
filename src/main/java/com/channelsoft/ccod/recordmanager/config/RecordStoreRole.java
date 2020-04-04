package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: RecordStoreRole
 * @Author: lanhb
 * @Description: 用来定义录音文件存储规则
 * @Date: 2020/4/4 22:28
 * @Version: 1.0
 */
public class RecordStoreRole {

    private String mntDir;  //录音盘阵的挂载目录

    private String grokPattern; //录音文件的完整grok表达式

    private String example; //录音文件在盘阵存储的一个实例

    private String recordIndex;  //该录音文件的录音索引

    private DateFormat dateFormat; //用来定义录音文件存储路径中的时间规则

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
