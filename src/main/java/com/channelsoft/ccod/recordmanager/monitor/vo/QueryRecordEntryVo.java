package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.util.Date;

public class QueryRecordEntryVo
{
	private String enterpriseId;
	private Date beginTime;
	private Date endTime;
	private int recordCount;
	private boolean isRandom;
	public QueryRecordEntryVo()
	{
		
	}
	public String getEnterpriseId()
	{
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId)
	{
		this.enterpriseId = enterpriseId;
	}
	public Date getBeginTime()
	{
		return beginTime;
	}
	public void setBeginTime(Date beginTime)
	{
		this.beginTime = beginTime;
	}
	public Date getEndTime()
	{
		return endTime;
	}
	public void setEndTime(Date endTime)
	{
		this.endTime = endTime;
	}
	public int getRecordCount()
	{
		return recordCount;
	}
	public void setRecordCount(int recordCount)
	{
		this.recordCount = recordCount;
	}
	public boolean isRandom()
	{
		return isRandom;
	}
	public void setRandom(boolean isRandom)
	{
		this.isRandom = isRandom;
	}
}
