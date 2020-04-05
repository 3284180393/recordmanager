package com.channelsoft.ccod.recordmanager.monitor.vo;

import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;

import java.util.List;

/**
 * @ClassName: RecordBackupResultVo
 * @Author: lanhb
 * @Description: 用来定义平台录音备份结果
 * @Date: 2020/4/5 11:22
 * @Version: 1.0
 */
public class RecordBackupResultVo {

    private String platformId;  //平台id

    private String platformName; //平台名

    private String backupDate; //备份的哪天的录音文件

    private boolean result; //执行备份操作是否成功

    private String comment; //如果备份失败，失败的原因

    private List<StoredRecordFileVo> backupList; //成功备份的录音文件

    private List<StoredRecordFileVo> failList; //失败备份的录音文件

    private boolean compareWithDB; //是否同数据库记录进行对比

    private List<RecordDetailVo> notBackupList; //如果需要同数据库记录对比,记录需要备份但没有备份的录音

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

    public String getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(String backupDate) {
        this.backupDate = backupDate;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<StoredRecordFileVo> getBackupList() {
        return backupList;
    }

    public void setBackupList(List<StoredRecordFileVo> backupList) {
        this.backupList = backupList;
    }

    public List<StoredRecordFileVo> getFailList() {
        return failList;
    }

    public void setFailList(List<StoredRecordFileVo> failList) {
        this.failList = failList;
    }

    public List<RecordDetailVo> getNotBackupList() {
        return notBackupList;
    }

    public void setNotBackupList(List<RecordDetailVo> notBackupList) {
        this.notBackupList = notBackupList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isCompareWithDB() {
        return compareWithDB;
    }

    public void setCompareWithDB(boolean compareWithDB) {
        this.compareWithDB = compareWithDB;
    }
}
