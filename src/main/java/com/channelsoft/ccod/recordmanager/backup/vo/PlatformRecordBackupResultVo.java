package com.channelsoft.ccod.recordmanager.backup.vo;

import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: PlatformRecordBackupResultVo
 * @Author: lanhb
 * @Description: 用来定义平台录音备份结果
 * @Date: 2020/4/6 14:35
 * @Version: 1.0
 */
public class PlatformRecordBackupResultVo {

    private String platformId;  //平台id

    private String platformName; //平台名

   private Date startBackupTime; //开始备份时间

    private int timeUsage; //用时

    private Date backupDate; //备份的哪一天录音

    private boolean result; //备份结果

    private List<StoredRecordFileVo> successList; //成功备份列表

    private List<StoredRecordFileVo> failList; //失败备份列表

    private List<RecordDetailVo> missRecordList; //应该备份而又未能备份的录音详情列表

    private String comment; //备注

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

    public Date getStartBackupTime() {
        return startBackupTime;
    }

    public void setStartBackupTime(Date startBackupTime) {
        this.startBackupTime = startBackupTime;
    }

    public int getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(int timeUsage) {
        this.timeUsage = timeUsage;
    }

    public Date getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(Date backupDate) {
        this.backupDate = backupDate;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<StoredRecordFileVo> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<StoredRecordFileVo> successList) {
        this.successList = successList;
    }

    public List<StoredRecordFileVo> getFailList() {
        return failList;
    }

    public void setFailList(List<StoredRecordFileVo> failList) {
        this.failList = failList;
    }

    public List<RecordDetailVo> getMissRecordList() {
        return missRecordList;
    }

    public void setMissRecordList(List<RecordDetailVo> missRecordList) {
        this.missRecordList = missRecordList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
