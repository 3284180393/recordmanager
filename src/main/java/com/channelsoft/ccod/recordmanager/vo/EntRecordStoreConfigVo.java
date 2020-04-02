package com.channelsoft.ccod.recordmanager.vo;

import java.util.Date;

public class EntRecordStoreConfigVo
{
	private String enterpriseId;                   //企业id
	private String enterpriseName;                 //企业名
	private String lastSuccDiskArrayIP;             //最后一次查询成功的盘阵服务器ssh连接字符串 
	private String lastSuccBkDiskArrayIP;           //最有一次查询成功的备份盘阵的ssh连接字符串
	private String lastSuccMntDir;           //最有一次成功查询的盘阵挂载目录
	private String lastSuccBkMntDir;         //最后一次成功查询的备份盘阵挂载目录
	private Date lastSuccCheckTime;                //最后一次成功查询时间
	public String getEnterpriseId()
	{
		return enterpriseId;
	}
	public void setEnterpriseId(String enterpriseId)
	{
		this.enterpriseId = enterpriseId;
	}
	public String getEnterpriseName()
	{
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName)
	{
		this.enterpriseName = enterpriseName;
	}
	public String getLastSuccDiskArrayIP()
	{
		return lastSuccDiskArrayIP;
	}
	public void setLastSuccDiskArrayIP(String lastSuccDiskArrayIP)
	{
		this.lastSuccDiskArrayIP = lastSuccDiskArrayIP;
	}
	public String getLastSuccBkDiskArrayIP()
	{
		return lastSuccBkDiskArrayIP;
	}
	public void setLastSuccBkDiskArrayIP(String lastSuccBkDiskArrayIP)
	{
		this.lastSuccBkDiskArrayIP = lastSuccBkDiskArrayIP;
	}
	public String getLastSuccMntDir()
	{
		return lastSuccMntDir;
	}
	public void setLastSuccMntDir(String lastSuccMntDir)
	{
		this.lastSuccMntDir = lastSuccMntDir;
	}
	public String getLastSuccBkMntDir()
	{
		return lastSuccBkMntDir;
	}
	public void setLastSuccBkMntDir(String lastSuccBkMntDir)
	{
		this.lastSuccBkMntDir = lastSuccBkMntDir;
	}
	public Date getLastSuccCheckTime()
	{
		return lastSuccCheckTime;
	}
	public void setLastSuccCheckTime(Date lastSuccCheckTime)
	{
		this.lastSuccCheckTime = lastSuccCheckTime;
	}
}
