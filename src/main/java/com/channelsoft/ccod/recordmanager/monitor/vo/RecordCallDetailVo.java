package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.util.Date;

public class RecordCallDetailVo
{
	private String enterpriseId;     //企业ID
	private Date callTime;           //呼叫开始时间
	private String sessionId;        //呼叫sessionId
	private int callType;            //呼叫类型
	private String agentId;          //呼叫坐席ID
	private String agentPassword;    //坐席登陆密码
	private String agentDn;          //坐席的设备分机
	private String localUrl;         //呼叫本地号码
	private String remoteUrl;        //呼叫远端号码 
	private int talkDuration;        //呼叫用时
	private String recordIndex;      //录音索引
	private int recordType;          //录音类型,0:未知,1:混音,2:并线
	public RecordCallDetailVo(TalkDetailVo talkDetailVo)
	{
		this.callTime = talkDetailVo.getStartTime();
		this.sessionId = talkDetailVo.getSessionId();
		this.callType = talkDetailVo.getCallType();
		this.agentId = talkDetailVo.getAgentId();
		this.agentDn = talkDetailVo.getAgentDn();
		this.localUrl = talkDetailVo.getLocalUrl();
		this.remoteUrl = talkDetailVo.getRemoteUrl();
		this.talkDuration = talkDetailVo.getTalkDuration();
	}
	public String getEnterpriseId()
	{
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId)
	{
		this.enterpriseId = enterpriseId;
	}
	public Date getCallTime()
	{
		return callTime;
	}
	public void setCallTime(Date callTime)
	{
		this.callTime = callTime;
	}
	public String getSessionId()
	{
		return sessionId;
	}
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
	public int getCallType()
	{
		return callType;
	}
	public void setCallType(int callType)
	{
		this.callType = callType;
	}
	public String getAgentId()
	{
		return agentId;
	}
	public void setAgentId(String agentId)
	{
		this.agentId = agentId;
	}
	public String getAgentPassword()
	{
		return agentPassword;
	}
	public void setAgentPassword(String agentPassword)
	{
		this.agentPassword = agentPassword;
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
	public String getRecordIndex()
	{
		return recordIndex;
	}
	public void setRecordIndex(String recordIndex)
	{
		this.recordIndex = recordIndex;
	}
	public int getRecordType()
	{
		return recordType;
	}
	public void setRecordType(int recordType)
	{
		this.recordType = recordType;
	}
}
