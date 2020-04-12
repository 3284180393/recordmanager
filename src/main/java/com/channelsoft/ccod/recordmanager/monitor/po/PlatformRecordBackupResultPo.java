package com.channelsoft.ccod.recordmanager.monitor.po;

import java.util.Date;

/**
 * @ClassName: PlatformRecordBackupResultPo
 * @Author: lanhb
 * @Description: 平台录音备份结果pojo类
 * @Date: 2020/4/12 12:50
 * @Version: 1.0
 */
public class PlatformRecordBackupResultPo {

    private int id;   //id，数据库自动生成

    private String platformId; //平台id

    private String platformName; //平台名

    private Date backupDate; //备份的哪一天平台录音

    private Date startTime; //开始备份时间

    private Date endTime; //备份结束时间

    private boolean result; //检查结果

    private String comment; //备注

    private int backupCount; //备份文件数目

    private int failCount; //失败

    private int notBackupCount; //应该备份而没有找到录音文件的数目

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Date getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(Date backupDate) {
        this.backupDate = backupDate;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getBackupCount() {
        return backupCount;
    }

    public void setBackupCount(int backupCount) {
        this.backupCount = backupCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public int getNotBackupCount() {
        return notBackupCount;
    }

    public void setNotBackupCount(int notBackupCount) {
        this.notBackupCount = notBackupCount;
    }
}
