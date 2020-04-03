package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.util.Date;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class EntRecordCheckResultVo
{
	private String enterpriseId;     //企业ID
	private String enterpriseName;   //企业名
	private int storeMethod;         //存储方式,1、非主备,2、主备
	private String sessionId;        //呼叫sessionId
	private Date startTime;          //呼叫开始时间  
	private Date endTime;            //呼叫结束时间  
	private int queueDuration;       //排队时长
	private int alertDuration;       //振铃时长
	private int acwDuration;	     //事后整理时长
	private int callType;            //呼叫类型
	private int endType;             //结束类型
	private String agentId;          //呼叫坐席ID
	private String agentDn;          //坐席的设备分机
	private String localUrl;         //呼叫本地号码
	private String remoteUrl;        //呼叫远端号码 
	private int talkDuration;        //通话时长
	private String recordIndex;      //录音索引
	private boolean isFileExist;     //录音文件是否存在
	private String recordIndexBak;   //备份录音索引
	private boolean isBkFileExist;   //备份录音文件是否存在
	private String dbName;           //保存记录的数据库名
	private String schemaName;       //保存记录的schema名
	private int checkResult;         //录音检查结果
	private String comment;          //检查结果备注
	private String diskArrayIP;      //存储盘阵IP
	private String saveMntDir;       //盘阵上存储挂载目录
	private String bkDiskArrayIP;    //备份盘阵IP
	private String bkSaveMntDir;     //备份盘阵存储挂载目录
	private String fastDfsUrl;       //如果是云平台记录录音文件的fastDfs url
	public EntRecordCheckResultVo()
	{
		
	}	
	public EntRecordCheckResultVo(EntRecordVo recordVo, GlsEntPo entPo)
	{
		this.enterpriseId = entPo.getEnterpriseId();
		this.enterpriseName = entPo.getEnterpriseName();
		this.sessionId = recordVo.getSessionId();
		this.startTime = recordVo.getStartTime();
		this.endTime = recordVo.getEndTime();
		this.queueDuration = recordVo.getQueueDuration();
		this.alertDuration = recordVo.getAlertDuration();
		this.callType = recordVo.getCallType();
		this.endType = recordVo.getEndType();
		this.agentId = recordVo.getAgentId();
		this.agentDn = recordVo.getAgentDn();
		this.localUrl = recordVo.getLocalUrl();
		this.remoteUrl = recordVo.getRemoteUrl();
		this.talkDuration = recordVo.getTalkDuration();
		if(StringUtils.isNotBlank(recordVo.getMixRecordIndex()))
		{
			this.recordIndex = recordVo.getMixRecordIndex();
		}
		else if(StringUtils.isNotBlank(recordVo.getCombineRecordIndex()))
		{
			this.recordIndex = recordVo.getCombineRecordIndex();
		}
		else
		{
			this.recordIndex = null;
		}
		this.recordIndexBak = recordVo.getRecordIndexBak();
		this.schemaName = recordVo.getSchemaName();
		this.dbName = recordVo.getDbName();
		this.isFileExist = false;
		this.isBkFileExist = false;
		this.fastDfsUrl = recordVo.getFastDfsUrl();
	}
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
	public String getRecordIndexBak()
	{
		return recordIndexBak;
	}
	public void setRecordIndexBak(String recordIndexBak)
	{
		this.recordIndexBak = recordIndexBak;
	}
	public int getCheckResult()
	{
		return checkResult;
	}
	public void setCheckResult(int checkResult)
	{
		this.checkResult = checkResult;
	}
	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	public String getRecordIndex()
	{
		return recordIndex;
	}
	public void setRecordIndex(String recordIndex)
	{
		this.recordIndex = recordIndex;
	}
	public String getDiskArrayIP()
	{
		return diskArrayIP;
	}
	public void setDiskArrayIP(String diskArrayIP)
	{
		this.diskArrayIP = diskArrayIP;
	}
	public String getSaveMntDir()
	{
		return saveMntDir;
	}
	public void setSaveMntDir(String saveMntDir)
	{
		this.saveMntDir = saveMntDir;
	}
	public String getBkDiskArrayIP()
	{
		return bkDiskArrayIP;
	}
	public void setBkDiskArrayIP(String bkDiskArrayIP)
	{
		this.bkDiskArrayIP = bkDiskArrayIP;
	}
	public String getBkSaveMntDir()
	{
		return bkSaveMntDir;
	}
	public void setBkSaveMntDir(String bkSaveMntDir)
	{
		this.bkSaveMntDir = bkSaveMntDir;
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
	public String getEnterpriseName()
	{
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName)
	{
		this.enterpriseName = enterpriseName;
	}
	public int getStoreMethod()
	{
		return storeMethod;
	}
	public void setStoreMethod(int storeMethod)
	{
		this.storeMethod = storeMethod;
	}
	
	@Override
	public String toString()
	{
		String jsonString = JSONObject.toJSONString(this);
		return jsonString;
	}
	public Boolean getIsFileExist()
	{
		return isFileExist;
	}
	public void setIsFileExist(Boolean isFileExist)
	{
		this.isFileExist = isFileExist;
	}
	public Boolean getIsBkFileExist()
	{
		return isBkFileExist;
	}
	public void setIsBkFileExist(Boolean isBkFileExist)
	{
		this.isBkFileExist = isBkFileExist;
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
