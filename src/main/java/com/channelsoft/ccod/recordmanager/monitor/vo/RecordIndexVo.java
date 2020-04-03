package com.channelsoft.ccod.recordmanager.monitor.vo;

public class RecordIndexVo
{
	private String enterpriseId;
	private String agentId;
	private String sessionId;
	private String recordIndex;
	private int recordType;
	private String storeUrl;
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
	public String getEnterpriseId()
	{
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId)
	{
		this.enterpriseId = enterpriseId;
	}
	public String getAgentId()
	{
		return agentId;
	}
	public void setAgentId(String agentId)
	{
		this.agentId = agentId;
	}
	public String getSessionId()
	{
		return sessionId;
	}
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
	public String getStoreUrl()
	{
		return storeUrl;
	}
	public void setStoreUrl(String storeUrl)
	{
		this.storeUrl = storeUrl;
	}
}
