package com.channelsoft.ccod.recordmanager.monitor.po;

import java.util.Date;

/**
 * @ClassName: PlatformRecordCheckResultPo
 * @Author: lanhb
 * @Description: 平台录音检查结果pojo类
 * @Date: 2020/4/11 19:19
 * @Version: 1.0
 */
public class PlatformRecordCheckResultPo {

    private int id;   //id，数据库自动生成

    private String platformId; //平台id

    private String platformName; //平台名

    private Date checkTime; //检查时间

    private int timeUsage; //用时

    private Date beginTime; //被检查时间段的开始时间

    private Date endTime; //被检查时间段的结束时间

    private boolean result; //检查结果

    private String comment; //备注

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

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
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

    public int getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(int timeUsage) {
        this.timeUsage = timeUsage;
    }
}
