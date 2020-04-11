package com.channelsoft.ccod.recordmanager.backup.vo;

import com.channelsoft.ccod.recordmanager.monitor.vo.EntRecordCheckResultVo;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private String backupDate; //备份的哪天的录音文件

    private Date beginTime; //开始备份时间

    private Date endTime; //结束备份时间

    private boolean result; //执行备份操作是否成功

    private String comment; //如果备份失败，失败的原因

    private List<StoredRecordFileVo> backupList; //成功备份的录音文件

    private List<StoredRecordFileVo> failList; //失败备份的录音文件

    private boolean compareWithDB; //是否同数据库记录进行对比

    private List<RecordDetailVo> notBackupList; //如果需要同数据库记录对比,记录需要备份但没有备份的录音

    public PlatformRecordBackupResultVo(
            String platformId, String platformName, Date backupDate, Date beginTime,
            List<StoredRecordFileVo> backupList, List<StoredRecordFileVo> failList)
    {
        this.result = true;
        this.platformId = platformId;
        this.platformName = platformName;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        this.backupDate = sf.format(backupDate);
        this.beginTime = beginTime;
        this.endTime = new Date();
        this.backupList = backupList;
        this.failList = failList;
        this.compareWithDB = false;
        this.notBackupList = new ArrayList<>();
        this.comment = toString();
    }

    public PlatformRecordBackupResultVo(
            String platformId, String platformName, Date backupDate, Date beginTime,
            List<StoredRecordFileVo> backupList, List<StoredRecordFileVo> failList, List<RecordDetailVo> notBackupList)
    {
        this.result = true;
        this.platformId = platformId;
        this.platformName = platformName;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        this.backupDate = sf.format(backupDate);
        this.beginTime = beginTime;
        this.endTime = new Date();
        this.backupList = backupList;
        this.failList = failList;
        this.compareWithDB = true;
        this.notBackupList = notBackupList;
        this.comment = toString();
    }

    private PlatformRecordBackupResultVo(String platformId, String platformName, Date backupDate, String errorMsg)
    {
        this.platformId = platformId;
        this.platformName = platformName;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        this.backupDate = sf.format(backupDate);
        this.comment = errorMsg;
    }

    public static PlatformRecordBackupResultVo fail(String platformId, String platformName, Date backupDate, Exception ex)
    {
        return new PlatformRecordBackupResultVo(platformId, platformName, backupDate, ex.getMessage());
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

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString()
    {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        if(!this.result)
        {
            return this.comment;
        }
        int allCount = this.backupList.size() + this.failList.size();
        this.comment = String.format("%s %s(%s)平台一共备份%s期间%d条录音文件,成功备份%d条",
                sf.format(now), this.platformName, this.platformId, this.backupDate, allCount, this.backupList.size());
        if(this.failList.size() > 0)
            this.comment = String.format("%s,%d条录音备份失败", this.comment, this.failList.size());
        if(this.compareWithDB)
        {
            if(notBackupList.size() > 0)
                this.comment = String.format("%s,同数据库呼叫记录比较%d条呼叫没有找到录音文件",
                        this.comment, this.notBackupList.size());
            else
                this.comment = String.format("%s,同数据库呼叫记录比较%d条呼叫没有遗漏", this.comment);
        }
        return this.comment;
    }
}
