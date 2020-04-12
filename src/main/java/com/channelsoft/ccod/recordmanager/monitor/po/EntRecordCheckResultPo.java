package com.channelsoft.ccod.recordmanager.monitor.po;

import java.util.Date;

/**
 * @ClassName: EntRecordCheckResultPo
 * @Author: lanhb
 * @Description: 用来定义企业录音检查结果的pojo类
 * @Date: 2020/4/12 14:28
 * @Version: 1.0
 */
public class EntRecordCheckResultPo {

    private int id;   //id，数据库自动生成

    private int platformCheckId; //对应的平台录音检查任务id

    private String enterpriseId; //企业id

    private String enterpriseName; //企业名

    private Date checkTime; //检查时间

    private Date beginTime; //被检查时间段的开始时间

    private Date endTime; //被检查时间段的结束时间

    private boolean result; //检查结果

    private String comment; //备注

    private boolean hasBak; //是否检查备分录音

    private int checkCount; //检查录音数

    private int successCount; //没有问题录音数目

    private int notIndexCount; //没有录音索引数目

    private int notFileCount; //没有录音文件数目

    private int notBakIndexCount; //没有被录音索引数目

    private int notBakFileCount; //没有被录音文件数目

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlatformCheckId() {
        return platformCheckId;
    }

    public void setPlatformCheckId(int platformCheckId) {
        this.platformCheckId = platformCheckId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
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

    public boolean isHasBak() {
        return hasBak;
    }

    public void setHasBak(boolean hasBak) {
        this.hasBak = hasBak;
    }

    public int getCheckCount() {
        return checkCount;
    }

    public void setCheckCount(int checkCount) {
        this.checkCount = checkCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getNotIndexCount() {
        return notIndexCount;
    }

    public void setNotIndexCount(int notIndexCount) {
        this.notIndexCount = notIndexCount;
    }

    public int getNotFileCount() {
        return notFileCount;
    }

    public void setNotFileCount(int notFileCount) {
        this.notFileCount = notFileCount;
    }

    public int getNotBakIndexCount() {
        return notBakIndexCount;
    }

    public void setNotBakIndexCount(int notBakIndexCount) {
        this.notBakIndexCount = notBakIndexCount;
    }

    public int getNotBakFileCount() {
        return notBakFileCount;
    }

    public void setNotBakFileCount(int notBakFileCount) {
        this.notBakFileCount = notBakFileCount;
    }
}
