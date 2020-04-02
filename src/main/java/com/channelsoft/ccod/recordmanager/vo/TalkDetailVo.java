package com.channelsoft.ccod.recordmanager.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TalkDetailVo
{
	private String sessionId;
	private Date startTime;
	private Date endTime;
	private String localUrl;
	private String remoteUrl;
	private String agentId;
	private String agentDn;
	private String agentName;
	private String skillDesc;
	private int queueDuration;
	private int alertDuration;
	private int talkDuration;
	private int acwDuration;
	private int endType;
	private int callType;
	private String recordIndex;
	private int recordType;
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
	public String getSkillDesc()
	{
		return skillDesc;
	}
	public void setSkillDesc(String skillDesc)
	{
		this.skillDesc = skillDesc;
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
	public int getTalkDuration()
	{
		return talkDuration;
	}
	public void setTalkDuration(int talkDuration)
	{
		this.talkDuration = talkDuration;
	}
	public int getAcwDuration()
	{
		return acwDuration;
	}
	public void setAcwDuration(int acwDuration)
	{
		this.acwDuration = acwDuration;
	}
	public int getEndType()
	{
		return endType;
	}
	public void setEndType(int endType)
	{
		this.endType = endType;
	}
	public String getAgentName()
	{
		return agentName;
	}
	public void setAgentName(String agentName)
	{
		this.agentName = agentName;
	}
	public int getCallType()
	{
		return callType;
	}
	public void setCallType(int callType)
	{
		this.callType = callType;
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
	@Override
	public String toString()
	{
		StringBuffer sBuffer = new StringBuffer();
		SimpleDateFormat sFormat = new SimpleDateFormat("HH:mm:ss");
		sBuffer.append(sFormat.format(startTime)).append("--").append(sFormat.format(endTime)).append(" ");
		if(callType == 1)
		{
			sBuffer.append(localUrl).append("外呼").append(remoteUrl).append("");
		}
		else if(callType == 0)
		{
			
		}
		else
		{
			sBuffer.append("未知呼叫类型" + callType);
		}
		return sBuffer.toString();
	}
}
