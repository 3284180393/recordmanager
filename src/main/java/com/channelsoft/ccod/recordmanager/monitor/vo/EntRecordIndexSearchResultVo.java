package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.util.Date;
import java.util.Map;

public class EntRecordIndexSearchResultVo
{
	private String entId;       //被查询的录音索引归属企业
	private Date checkTime;   //录音索引检查时间
	private boolean isSucc;     //是否查询成功,只要有一个索引被找到即认为查询成功
	private String host;        //该企业录音所在的主机
	private String dir;         //该企业录音所在的目录
	private Map<String, String> existIndexMap;  //被查询到的企业录音索引,map中key为被查询的录音索引,value可以根据需要进行赋值
	private String comment; //备注
	public String getEntId()
	{
		return entId;
	}
	public void setEntId(String entId)
	{
		this.entId = entId;
	}
	public boolean isSucc()
	{
		return isSucc;
	}
	public void setSucc(boolean isSucc)
	{
		this.isSucc = isSucc;
	}
	public String getHost()
	{
		return host;
	}
	public void setHost(String host)
	{
		this.host = host;
	}
	public String getDir()
	{
		return dir;
	}
	public void setDir(String dir)
	{
		this.dir = dir;
	}
	public Map<String, String> getExistIndexMap()
	{
		return existIndexMap;
	}
	public void setExistIndexMap(Map<String, String> existIndexMap)
	{
		this.existIndexMap = existIndexMap;
	}
	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	public Date getCheckTime()
	{
		return checkTime;
	}
	public void setCheckTime(Date checkTime)
	{
		this.checkTime = checkTime;
	}
}
