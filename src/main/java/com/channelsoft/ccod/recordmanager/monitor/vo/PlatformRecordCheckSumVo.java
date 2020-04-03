package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class PlatformRecordCheckSumVo
{
	private int id;
	private String platformId;
	private String platformName;
	private int storeMethod;
	private int enterpriseType;
	private Date checkBeginTime;
	private Date checkEndTime;
	private Date callBeginTime;
	private Date callEndTime;
	private int checkCount;
	private int relateAgentCount;
	private int normalCount;
	private int abnormalCount;
	private int indexLossCount;
	private int fileLossCount;
	private int sessionDetailLossCount;
	private int bkIndexLossCount;
	private int bkFileLossCount;
	private int checkResult;
	private List<EntRecordCheckSumVo> entRecordCheckSumList;
//	private List<ProblemNoticeMsgVo> alarmList;
	private String comment;
	private boolean isAlarm;

	public PlatformRecordCheckSumVo(String platformId, String platformName,
			int storeMethod, int enterpriseType, Date checkTime,
			Date beginTime, Date endTime)
	{
		this.platformId = platformId;
		this.platformName = platformName;
		this.storeMethod = storeMethod;
		this.enterpriseType = enterpriseType;
		this.checkBeginTime = checkTime;
		this.callBeginTime = beginTime;
		this.callEndTime = endTime;
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

	public int getEnterpriseType()
	{
		return enterpriseType;
	}

	public void setEnterpriseType(int enterpriseType)
	{
		this.enterpriseType = enterpriseType;
	}
	public Date getCheckBeginTime()
	{
		return checkBeginTime;
	}

	public void setCheckBeginTime(Date checkBeginTime)
	{
		this.checkBeginTime = checkBeginTime;
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

	public int getCheckCount()
	{
		return checkCount;
	}

	public void setCheckCount(int checkCount)
	{
		this.checkCount = checkCount;
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

	public int getCheckResult()
	{
		return checkResult;
	}

	public void setCheckResult(int checkResult)
	{
		this.checkResult = checkResult;
	}

	public List<EntRecordCheckSumVo> getEntRecordCheckSumList()
	{
		return entRecordCheckSumList;
	}

	public void setEntRecordCheckSumList(
			List<EntRecordCheckSumVo> entRecordCheckSumList)
	{
		this.entRecordCheckSumList = entRecordCheckSumList;
	}

	public String getComment()
	{
		return comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public boolean isAlarm()
	{
		return isAlarm;
	}

	public void setAlarm(boolean isAlarm)
	{
		this.isAlarm = isAlarm;
	}

//	public List<ProblemNoticeMsgVo> getAlarmList()
//	{
//		return alarmList;
//	}
//
//	public void setAlarmList(List<ProblemNoticeMsgVo> alarmList)
//	{
//		this.alarmList = alarmList;
//	}

	public int getNormalCount()
	{
		return normalCount;
	}

	public void setNormalCount(int normalCount)
	{
		this.normalCount = normalCount;
	}

	public Date getCheckEndTime()
	{
		return checkEndTime;
	}

	public void setCheckEndTime(Date checkEndTime)
	{
		this.checkEndTime = checkEndTime;
	}

	public int getAbnormalCount()
	{
		return abnormalCount;
	}

	public void setAbnormalCount(int abnormalCount)
	{
		this.abnormalCount = abnormalCount;
	}
	
	public int getRelateAgentCount()
	{
		return relateAgentCount;
	}

	public void setRelateAgentCount(int relateAgentCount)
	{
		this.relateAgentCount = relateAgentCount;
	}

	@Override
	public String toString()
	{
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(sFormat.format(checkBeginTime)).append("到")
				.append(sFormat.format(checkEndTime));
		sBuilder.append("用时")
				.append((checkEndTime.getTime() - checkBeginTime.getTime()) / 1000)
				.append("(秒)");
		sBuilder.append("完成对").append(platformName).append("(")
				.append(platformId).append(")");
		sBuilder.append("的").append(sFormat.format(callBeginTime)).append("-")
				.append(sFormat.format(callEndTime)).append("时间段录音检查,检查结果为");
//		PlatformRecordCheckResult checkRst = PlatformRecordCheckResult
//				.getEnum(checkResult);
//		sBuilder.append(checkRst.desc).append(".");
//		if (checkRst.equals(EntCheckRecordResult.NoCall)
//				|| entRecordCheckSumList == null
//				|| entRecordCheckSumList.size() == 0)
//		{
//			return sBuilder.toString();
//		}
		sBuilder.append("总共检查").append(entRecordCheckSumList.size())
				.append("个企业").append(checkCount).append("条呼叫");
		sBuilder.append(",").append(normalCount).append("正常,");
		if(sessionDetailLossCount > 0)
		{
			sBuilder.append(sessionDetailLossCount).append("条呼叫有录音索引没有录音明细,");
		}
		sBuilder.append(indexLossCount).append("录音索引丢失,");
		sBuilder.append(fileLossCount).append("录音文件丢失,");
//		if (storeMethod == EntRecordStoreMethod.HasBak.id)
//		{
//			sBuilder.append(bkIndexLossCount).append("备份录音索引丢失,");
//			sBuilder.append(bkFileLossCount).append("备份录音文件丢失.");
//		}
		return sBuilder.toString();
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
