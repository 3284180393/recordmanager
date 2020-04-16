package com.channelsoft.ccod.recordmanager.monitor.vo;

import com.channelsoft.ccod.recordmanager.constant.RecordType;

import java.util.Date;

/**
 * @ClassName: RecordDetailVo
 * @Author: lanhb
 * @Description: 用来定义录音明细的类
 * @Date: 2020/4/4 13:06
 * @Version: 1.0
 */
public class RecordDetailVo {

    private String enterpriseId;     //企业ID

    private String agentId; //座席工号

    private String sessionId;        //呼叫sessionId

    private Date startTime;          //呼叫开始时间

    private Date endTime;            //呼叫结束时间

    private int callType;            //呼叫类型

    private int endType;              //结束类型

    private int talkDuration;        //呼叫用时

    private RecordType recordType; //录音类型

    private String recordIndex;   //录音索引

    private String recordFileFastDfsUrl; //索引对应的录音文件存放url,主要是4.5平台fastdfs存储

    private boolean hasBak; //是否有备份录音

    private String bakRecordIndex;    //备份录音索引

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public int getEndType() {
        return endType;
    }

    public void setEndType(int endType) {
        this.endType = endType;
    }

    public int getTalkDuration() {
        return talkDuration;
    }

    public void setTalkDuration(int talkDuration) {
        this.talkDuration = talkDuration;
    }

    public RecordType getRecordType() {
        return recordType;
    }

    public void setRecordType(RecordType recordType) {
        this.recordType = recordType;
    }

    public String getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(String recordIndex) {
        this.recordIndex = recordIndex;
    }

    public boolean isHasBak() {
        return hasBak;
    }

    public void setHasBak(boolean hasBak) {
        this.hasBak = hasBak;
    }

    public String getBakRecordIndex() {
        return bakRecordIndex;
    }

    public void setBakRecordIndex(String bakRecordIndex) {
        this.bakRecordIndex = bakRecordIndex;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getRecordFileFastDfsUrl() {
        return recordFileFastDfsUrl;
    }

    public void setRecordFileFastDfsUrl(String recordFileFastDfsUrl) {
        this.recordFileFastDfsUrl = recordFileFastDfsUrl;
    }
}
