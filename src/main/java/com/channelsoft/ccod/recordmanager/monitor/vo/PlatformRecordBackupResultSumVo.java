package com.channelsoft.ccod.recordmanager.monitor.vo;

import com.channelsoft.ccod.recordmanager.backup.vo.StoredRecordFileVo;
import com.channelsoft.ccod.recordmanager.monitor.po.FailBackupRecordFilePo;
import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordBackupResultPo;
import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordCheckResultPo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: PlatformRecordBackupResultSumVo
 * @Author: lanhb
 * @Description: 用来保存平台备份结果的类
 * @Date: 2020/4/13 15:59
 * @Version: 1.0
 */
public class PlatformRecordBackupResultSumVo {

    private String platformId;  //平台id

    private String platformName; //平台名

    private Date backupDate; //备份的哪天的录音文件

    private Date beginTime; //开始备份时间

    private Date endTime; //结束备份时间

    private boolean result; //执行备份操作是否成功

    private String comment; //如果备份失败，失败的原因

    private int backupCount; //备份录音文件数目

    private int successBackupCount; //成功备份的录音文件

    private List<FailBackupRecordFilePo> failList; //失败备份的录音文件

    private boolean compareWithDB; //是否同数据库记录进行对比

    private List<RecordDetailVo> notBackupList; //如果需要同数据库记录对比,记录需要备份但没有备份的录音

    public PlatformRecordBackupResultSumVo(
            String platformId, String platformName, Date backupDate, Date beginTime,
            List<StoredRecordFileVo> backupList, List<FailBackupRecordFilePo> failList)
    {
        this.result = true;
        this.platformId = platformId;
        this.platformName = platformName;
        this.backupDate = backupDate;
        this.beginTime = beginTime;
        this.endTime = new Date();
        this.backupCount = backupList.size();
        this.successBackupCount = backupList.size() - failList.size();
        this.failList = failList;
        this.compareWithDB = false;
        this.notBackupList = new ArrayList<>();
        this.comment = toString();
    }

    public PlatformRecordBackupResultSumVo(
            String platformId, String platformName, Date backupDate, Date beginTime,
            List<StoredRecordFileVo> backupList, List<FailBackupRecordFilePo> failList, List<RecordDetailVo> notBackupList)
    {
        this.result = true;
        this.platformId = platformId;
        this.platformName = platformName;
        this.backupDate = backupDate;
        this.beginTime = beginTime;
        this.endTime = new Date();
        this.backupCount = backupList.size();
        this.successBackupCount = backupList.size() - failList.size();
        this.failList = failList;
        this.compareWithDB = true;
        this.notBackupList = notBackupList;
        this.comment = toString();
    }

    private PlatformRecordBackupResultSumVo(String platformId, String platformName, Date backupDate, String errorMsg)
    {
        this.platformId = platformId;
        this.platformName = platformName;
        this.backupDate = backupDate;
        this.comment = errorMsg;
        this.failList = new ArrayList<>();
    }

    public static PlatformRecordBackupResultSumVo fail(String platformId, String platformName, Date backupDate, Exception ex)
    {
        return new PlatformRecordBackupResultSumVo(platformId, platformName, backupDate, ex.getMessage());
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

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getBackupCount() {
        return backupCount;
    }

    public void setBackupCount(int backupCount) {
        this.backupCount = backupCount;
    }

    public int getSuccessBackupCount() {
        return successBackupCount;
    }

    public void setSuccessBackupCount(int successBackupCount) {
        this.successBackupCount = successBackupCount;
    }

    public List<FailBackupRecordFilePo> getFailList() {
        return failList;
    }

    public void setFailList(List<FailBackupRecordFilePo> failList) {
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
        SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        if(!this.result) {
            return this.comment;
        }
        this.comment = String.format("%s %s(%s)平台一共备份%s期间%d条录音文件,成功备份%d条",
                sf.format(now), this.platformName, this.platformId, sf.format(this.backupDate), this.backupCount, this.successBackupCount);
        if(this.failList.size() > 0)
            this.comment = String.format("%s,%d条录音备份失败", this.comment, this.failList.size());
        if(this.compareWithDB)
        {
            if(notBackupList.size() > 0)
                this.comment = String.format("%s,同数据库呼叫记录比较%d条呼叫录音文件应该备份而未备份",
                        this.comment, this.notBackupList.size());
            else
                this.comment = String.format("%s,同数据库呼叫记录比较所有呼叫录音文件都已经备份", this.comment);
        }
        return this.comment;
    }

    public PlatformRecordBackupResultPo getBackupResult()
    {
        PlatformRecordBackupResultPo po = new PlatformRecordBackupResultPo();
        po.setPlatformId(this.platformId);
        po.setPlatformName(this.platformName);
        po.setBackupDate(this.backupDate);
        po.setStartTime(this.beginTime);
        po.setEndTime(this.endTime);
        po.setResult(this.result);
        po.setComment(this.comment);
        po.setBackupCount(this.backupCount);
        po.setFailCount(this.failList.size());
        po.setNotBackupCount(this.notBackupList.size());
        return po;
    }
}
