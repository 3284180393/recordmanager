package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.util.Date;

public class EntRecordVo
{
	private String enterpriseId;     //企业ID
	private String sessionId;        //呼叫sessionId
	private Date startTime;          //呼叫开始时间  
	private Date endTime;            //呼叫结束时间  
	private int queueDuration;       //排队时长
	private int alertDuration;       //振铃时长
	private int acwDuration;	     //事后整理时长
	private int callType;            //呼叫类型
	private int endType;              //结束类型
	private String agentId;          //呼叫坐席ID
	private String agentDn;          //坐席的设备分机
	private String localUrl;         //呼叫本地号码
	private String remoteUrl;        //呼叫远端号码 
	private int talkDuration;        //呼叫用时
	private String mixRecordIndex;   //混音录音索引
	private String combineRecordIndex;//并线录音索引
	private String recordIndexBak;    //备份录音
	private String dbName;            //记录呼叫的dbName
	private String schemaName;        //dbName对应的schema
	private String fastDfsUrl;        //云版本录音文件存储的fastdfs路径
	public String getEnterpriseId()
	{
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId)
	{
		this.enterpriseId = enterpriseId;
	}
	public String getSessionId()
	{
		return sessionId;
	}
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
	public Date getStartTime()
	{
		return startTime;
	}
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}
	public Date getEndTime()
	{
		return endTime;
	}
	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}
	public int getQueueDuration()
	{
		return queueDuration;
	}
	public void setQueueDuration(int queueDuration)
	{
		this.queueDuration = queueDuration;
	}
	public int getAlertDuration()
	{
		return alertDuration;
	}
	public void setAlertDuration(int alertDuration)
	{
		this.alertDuration = alertDuration;
	}
	public int getAcwDuration()
	{
		return acwDuration;
	}
	public void setAcwDuration(int acwDuration)
	{
		this.acwDuration = acwDuration;
	}
	public int getCallType()
	{
		return callType;
	}
	public void setCallType(int callType)
	{
		this.callType = callType;
	}
	public int getEndType()
	{
		return endType;
	}
	public void setEndType(int endType)
	{
		this.endType = endType;
	}
	public String getAgentId()
	{
		return agentId;
	}
	public void setAgentId(String agentId)
	{
		this.agentId = agentId;
	}
	public String getAgentDn()
	{
		return agentDn;
	}
	public void setAgentDn(String agentDn)
	{
		this.agentDn = agentDn;
	}
	public String getLocalUrl()
	{
		return localUrl;
	}
	public void setLocalUrl(String localUrl)
	{
		this.localUrl = localUrl;
	}
	public String getRemoteUrl()
	{
		return remoteUrl;
	}
	public void setRemoteUrl(String remoteUrl)
	{
		this.remoteUrl = remoteUrl;
	}
	public int getTalkDuration()
	{
		return talkDuration;
	}
	public void setTalkDuration(int talkDuration)
	{
		this.talkDuration = talkDuration;
	}
	public String getMixRecordIndex()
	{
		return mixRecordIndex;
	}
	public void setMixRecordIndex(String mixRecordIndex)
	{
		this.mixRecordIndex = mixRecordIndex;
	}
	public String getCombineRecordIndex()
	{
		return combineRecordIndex;
	}
	public void setCombineRecordIndex(String combineRecordIndex)
	{
		this.combineRecordIndex = combineRecordIndex;
	}
	public String getRecordIndexBak()
	{
		return recordIndexBak;
	}
	public void setRecordIndexBak(String recordIndexBak)
	{
		this.recordIndexBak = recordIndexBak;
	}
	public String getDbName()
	{
		return dbName;
	}
	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}
	public String getSchemaName()
	{
		return schemaName;
	}
	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}
	public String getFastDfsUrl()
	{
		return fastDfsUrl;
	}
	public void setFastDfsUrl(String fastDfsUrl)
	{
		this.fastDfsUrl = fastDfsUrl;
	}
}
