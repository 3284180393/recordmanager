package com.channelsoft.ccod.recordmanager.monitor.po;

import java.util.Date;

/**
 * @ClassName: FailBackupRecordFilePo
 * @Author: lanhb
 * @Description: 用来定义备份失败录音文件的pojo类
 * @Date: 2020/4/12 21:23
 * @Version: 1.0
 */
public class FailBackupRecordFilePo {

    private int id;  //id数据库主键

    private int platformBackupId; //对应的备份任务id

    private Date recordDate; //录音日期

    private String fileSavePath; //文件存储路径

    private String backupPath; //文件将会备份大目的路径

    private String failReason; //文件备份失败原因

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlatformBackupId() {
        return platformBackupId;
    }

    public void setPlatformBackupId(int platformBackupId) {
        this.platformBackupId = platformBackupId;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
