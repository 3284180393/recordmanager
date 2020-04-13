package com.channelsoft.ccod.recordmanager.monitor.po;

import com.channelsoft.ccod.recordmanager.constant.RecordCheckResult;
import com.channelsoft.ccod.recordmanager.monitor.vo.RecordDetailVo;

import java.util.Date;

/**
 * @ClassName: MissBackupRecordDetailPo
 * @Author: lanhb
 * @Description: 用来定义应该备份但未备份的录音明细pojo类
 * @Date: 2020/4/12 19:06
 * @Version: 1.0
 */
public class MissBackupRecordDetailPo {

    private int id; //数据库生成唯一主键

    private int backupId; //平台录音备份id

    private String enterpriseId;     //企业ID

    private String enterpriseName; //企业名

    private String sessionId;        //呼叫sessionId

    private String agentId; //座席工号

    private Date startTime;          //呼叫开始时间

    private Date endTime;            //呼叫结束时间

    private int talkDuration;        //呼叫用时

    private int callType;            //呼叫类型

    private int endType;              //结束类型

    private String recordIndex;   //录音索引

    private String bakRecordIndex;    //备份录音索引

    private String failReason; //检查失败原因

    public MissBackupRecordDetailPo()
    {

    }

    public MissBackupRecordDetailPo(RecordDetailVo detailVo)
    {
        this.enterpriseId = detailVo.getEnterpriseId();
        this.sessionId = detailVo.getSessionId();
        this.agentId = detailVo.getAgentId();
        this.startTime = detailVo.getStartTime();
        this.endTime = detailVo.getEndTime();
        this.talkDuration = detailVo.getTalkDuration();
        this.callType = detailVo.getCallType();
        this.endType = detailVo.getEndType();
        this.recordIndex = detailVo.getRecordIndex();
        this.bakRecordIndex = detailVo.getBakRecordIndex();
        this.failReason = "NOT_BACKUP";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public int getTalkDuration() {
        return talkDuration;
    }

    public void setTalkDuration(int talkDuration) {
        this.talkDuration = talkDuration;
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

    public String getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(String recordIndex) {
        this.recordIndex = recordIndex;
    }

    public String getBakRecordIndex() {
        return bakRecordIndex;
    }

    public void setBakRecordIndex(String bakRecordIndex) {
        this.bakRecordIndex = bakRecordIndex;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public int getBackupId() {
        return backupId;
    }

    public void setBackupId(int backupId) {
        this.backupId = backupId;
    }
}
