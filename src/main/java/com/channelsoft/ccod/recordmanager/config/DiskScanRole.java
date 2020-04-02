package com.channelsoft.ccod.recordmanager.config;

/**
 * @ClassName: DiskScanRole
 * @Author: lanhb
 * @Description: 用来定义一条磁盘目录录音文件扫描规则
 * @Date: 2020/4/1 21:02
 * @Version: 1.0
 */
public class DiskScanRole {

    private String mntDir;  //录音盘阵的挂载目录

    private String grokPattern; //录音文件的完整grok表达式

    private String example; //录音文件在盘阵存储的一个实例

    private String recordIndex;  //该录音文件的录音索引

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
}
