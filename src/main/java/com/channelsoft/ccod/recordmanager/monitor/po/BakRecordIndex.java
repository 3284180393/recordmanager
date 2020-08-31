package com.channelsoft.ccod.recordmanager.monitor.po;

import java.io.Serializable;
import java.util.Date;

/**
 * (BakRecordIndex)实体类
 *
 * @author makejava
 * @since 2020-08-24 18:45:13
 */
public class BakRecordIndex implements Serializable {
    private static final long serialVersionUID = 879291591678945305L;

    private String recordName;

    private String entId;

    private String sessionId;

    private String remoteUri;

    private String localUri;

    private String agentId;

    private String cmsName;

    private int callType;

    private String deviceNumber;

    private Date startTime;

    private Date endTime;

    private Date ctiEndTime;

    private Date ctiStartTime;

    private String skillName;


    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRemoteUri() {
        return remoteUri;
    }

    public void setRemoteUri(String remoteUri) {
        this.remoteUri = remoteUri;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCmsName() {
        return cmsName;
    }

    public void setCmsName(String cmsName) {
        this.cmsName = cmsName;
    }

    public int getCallType() {
        return callType;
    }

    public void setCallType(int callType) {
        this.callType = callType;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
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

    public Date getCtiEndTime() {
        return ctiEndTime;
    }

    public void setCtiEndTime(Date ctiEndTime) {
        this.ctiEndTime = ctiEndTime;
    }

    public Date getCtiStartTime() {
        return ctiStartTime;
    }

    public void setCtiStartTime(Date ctiStartTime) {
        this.ctiStartTime = ctiStartTime;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getEntId() {
        return entId;
    }

    public void setEntId(String entId) {
        this.entId = entId;
    }
}