package com.channelsoft.ccod.recordmanager.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntRecordCheckSumVo
{
	private int id;
	private String platformId;
	private String platformName;
	private String enterpriseId;
	private String enterpriseName;
	private Date checkBeginTime;
	private Date checkEndTime;
	private Date callBeginTime;
	private Date callEndTime;
	private int storeMethod;
	private int checkResult;
	private int checkCount;
	private int relateAgentCount;
	private int normalCount;
	private int indexLossCount;
	private int fileLossCount;
	private int sessionDetailLossCount;
	private int bkIndexLossCount;
	private int bkFileLossCount;
	private int abnormalCount;
	private int lossRate;
	private String comment;
	private String diskArrayIP;
	private String mntDir;
	private String bkDiskArrayIP;
	private String bkMntDir;
	private List<EntRecordCheckResultVo> abnormalResultList;
	private boolean isAlarm;
	private String alarmMsg;

	public EntRecordCheckSumVo()
	{
		
	}
	public EntRecordCheckSumVo(String enterpriseId, String enterpriseName,
			Date checkBeginTime, Date checkEndTime, Date callBeginTime,
			Date callEndTime, int storeMethod)
	{
		this.enterpriseId = enterpriseId;
		this.enterpriseName = enterpriseName;
		this.checkBeginTime = checkBeginTime;
		this.checkEndTime = checkEndTime;
		this.callBeginTime = callBeginTime;
		this.callEndTime = callEndTime;
		this.storeMethod = storeMethod;
		this.abnormalResultList = new ArrayList<EntRecordCheckResultVo>();
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getPlatformId()
	{
		return platformId;
	}

	public void setPlatformId(String platformId)
	{
		this.platformId = platformId;
	}

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

	public int getCheckResult()
	{
		return checkResult;
	}

	public void setCheckResult(int checkResult)
	{
		this.checkResult = checkResult;
	}

	public int getCheckCount()
	{
		return checkCount;
	}

	public void setCheckCount(int checkCount)
	{
		this.checkCount = checkCount;
	}

	public int getNormalCount()
	{
		return normalCount;
	}

	public void setNormalCount(int normalCount)
	{
		this.normalCount = normalCount;
	}

	public int getIndexLossCount()
	{
		return indexLossCount;
	}

	public void setIndexLossCount(int indexLossCount)
	{
		this.indexLossCount = indexLossCount;
	}

	public int getFileLossCount()
	{
		return fileLossCount;
	}

	public void setFileLossCount(int fileLossCount)
	{
		this.fileLossCount = fileLossCount;
	}

	public int getBkIndexLossCount()
	{
		return bkIndexLossCount;
	}

	public void setBkIndexLossCount(int bkIndexLossCount)
	{
		this.bkIndexLossCount = bkIndexLossCount;
	}

	public int getBkFileLossCount()
	{
		return bkFileLossCount;
	}

	public void setBkFileLossCount(int bkFileLossCount)
	{
		this.bkFileLossCount = bkFileLossCount;
	}

	public List<EntRecordCheckResultVo> getAbnormalResultList()
	{
		return abnormalResultList;
	}

	public void setAbnormalResultList(
			List<EntRecordCheckResultVo> abnormalResultList)
	{
		this.abnormalResultList = abnormalResultList;
		if (abnormalResultList != null)
		{
			this.abnormalCount = abnormalResultList.size();
		}
		else
		{
			this.abnormalCount = 0;
		}
	}

	public String getPlatformName()
	{
		return platformName;
	}

	public void setPlatformName(String platformName)
	{
		this.platformName = platformName;
	}

	public int getStoreMethod()
	{
		return storeMethod;
	}

	public void setStoreMethod(int storeMethod)
	{
		this.storeMethod = storeMethod;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public int getAbnormalCount()
	{
		return abnormalCount;
	}

	public void setAbnormalCount(int abnormalCount)
	{
		this.abnormalCount = abnormalCount;
	}

	public int getLossRate()
	{
		return lossRate;
	}

	public void setLossRate(int lossRate)
	{
		this.lossRate = lossRate;
	}

	public Date getCheckBeginTime()
	{
		return checkBeginTime;
	}

	public void setCheckBeginTime(Date checkBeginTime)
	{
		this.checkBeginTime = checkBeginTime;
	}

	public Date getCheckEndTime()
	{
		return checkEndTime;
	}

	public void setCheckEndTime(Date checkEndTime)
	{
		this.checkEndTime = checkEndTime;
	}

	public String getDiskArrayIP()
	{
		return diskArrayIP;
	}

	public void setDiskArrayIP(String diskArrayIP)
	{
		this.diskArrayIP = diskArrayIP;
	}

	public String getMntDir()
	{
		return mntDir;
	}

	public void setMntDir(String mntDir)
	{
		this.mntDir = mntDir;
	}

	public String getBkDiskArrayIP()
	{
		return bkDiskArrayIP;
	}

	public void setBkDiskArrayIP(String bkDiskArrayIP)
	{
		this.bkDiskArrayIP = bkDiskArrayIP;
	}

	public String getBkMntDir()
	{
		return bkMntDir;
	}

	public void setBkMntDir(String bkMntDir)
	{
		this.bkMntDir = bkMntDir;
	}

	public Date getCallBeginTime()
	{
		return callBeginTime;
	}

	public void setCallBeginTime(Date callBeginTime)
	{
		this.callBeginTime = callBeginTime;
	}

	public Date getCallEndTime()
	{
		return callEndTime;
	}

	public void setCallEndTime(Date callEndTime)
	{
		this.callEndTime = callEndTime;
	}

	public int getRelateAgentCount()
	{
		return relateAgentCount;
	}

	public void setRelateAgentCount(int relateAgentCount)
	{
		this.relateAgentCount = relateAgentCount;
	}

	public boolean isAlarm()
	{
		return isAlarm;
	}

	public void setAlarm(boolean isAlarm)
	{
		this.isAlarm = isAlarm;
	}

	public String getAlarmMsg()
	{
		return alarmMsg;
	}

	public void setAlarmMsg(String alarmMsg)
	{
		this.alarmMsg = alarmMsg;
	}
	public int getSessionDetailLossCount()
	{
		return sessionDetailLossCount;
	}
	public void setSessionDetailLossCount(int sessionDetailLossCount)
	{
		this.sessionDetailLossCount = sessionDetailLossCount;
	}
}
