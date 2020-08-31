package com.channelsoft.ccod.recordmanager.monitor.vo;

import com.channelsoft.ccod.recordmanager.monitor.po.BakRecordIndex;
import com.channelsoft.ccod.recordmanager.monitor.po.PlatformRecordCheckResultPo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: /home/recordmanager/logs
 * @Author: lanhb
 * @Description: 平台录音检查结果汇总
 * @Date: 2020/4/11 19:13
 * @Version: 1.0
 */
public class PlatformRecordCheckResultSumVo {

    private String platformId;

    private String platformName;

    private Date checkTime;

    private int timeUsage;

    private Date startTime;

    private Date endTime;

    private boolean result;

    private List<EntRecordCheckResultSumVo> entRecordCheckResultList;

    private List<BakRecordIndex> hasBakNotMasterList; //有备索引没有主索引的备索引明细

    private String comment;

    private boolean isNotify;

    public PlatformRecordCheckResultSumVo(String platformId, String platformName, Date checkTime, Date startTime, Date endTime, List<EntRecordCheckResultSumVo> entRecordCheckResultList)
    {
        this.platformId = platformId;
        this.platformName = platformName;
        this.checkTime = checkTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.entRecordCheckResultList = entRecordCheckResultList;
        this.result = true;
        Date now = new Date();
        this.timeUsage = (int)(now.getTime() - checkTime.getTime())/1000;
        this.comment = toString();
        this.isNotify = !this.result;
    }

    protected PlatformRecordCheckResultSumVo(String platformId, String platformName, boolean result, String comment)
    {
        this.platformId = platformId;
        this.platformName = platformName;
        this.result = result;
        this.comment = comment;
        this.entRecordCheckResultList = new ArrayList<>();
        this.isNotify = !this.result;
    }

    public static PlatformRecordCheckResultSumVo fail(String platformId, String platformName, String errorMsg)
    {
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String comment = String.format("[%s]检查%s(%s)录音异常:%s", sf.format(now), platformId, platformName, errorMsg);
        return new PlatformRecordCheckResultSumVo(platformId, platformName, false, comment);
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public int getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(int timeUsage) {
        this.timeUsage = timeUsage;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<EntRecordCheckResultSumVo> getEntRecordCheckResultList() {
        return entRecordCheckResultList;
    }

    public void setEntRecordCheckResultList(List<EntRecordCheckResultSumVo> entRecordCheckResultList) {
        this.entRecordCheckResultList = entRecordCheckResultList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }


    public List<BakRecordIndex> getHasBakNotMasterList() {
        return hasBakNotMasterList;
    }

    public void setHasBakNotMasterList(List<BakRecordIndex> hasBakNotMasterList) {
        this.hasBakNotMasterList = hasBakNotMasterList;
    }

    @Override
    public String toString()
    {
        if(!this.result)
            return this.comment;
        int checkEntCount = this.entRecordCheckResultList.size();
        int failEntCount = 0;
        int successEntCount = 0;
        int allCount = 0;
        int successCount = 0;
        int notIndexCount = 0;
        int notFileCount = 0;
        int notBkIndexCount = 0;
        int notBkFileCount = 0;
        for(EntRecordCheckResultSumVo resultVo : this.entRecordCheckResultList)
        {
            if(!resultVo.isResult())
            {
                failEntCount++;
            }
            else
            {
                successEntCount++;
                allCount += resultVo.getAllRecordCount();
                successCount += resultVo.getSuccessList().size();
                notIndexCount += resultVo.getNotIndexList().size();
                notFileCount += resultVo.getNotFileList().size();
                notBkIndexCount += resultVo.getNotBakIndexList().size();
                notBkFileCount += resultVo.getNotBakFileList().size();
            }
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String msg = String.format("[%s] %s(%s)共检查%d个企业:", sf.format(checkTime), platformName, platformId, checkEntCount);
        if(successEntCount > 0)
        {
            if(allCount > 0)
            {
                msg = String.format("%d个企业涉及%d通电话,%d通录音正常,", successEntCount, allCount, successCount);
                if(notIndexCount > 0)
                    msg = String.format("%s%d录音索引丢失,", msg, notIndexCount);
                if(notFileCount > 0)
                    msg = String.format("%s%d录音文件丢失,", msg, notFileCount);
                if(notBkIndexCount > 0)
                    msg = String.format("%s%d备份录音索引丢失,", msg, notBkIndexCount);
                if(notBkFileCount > 0)
                    msg = String.format("%s%d备份录音文件丢失,", msg, notBkFileCount);
            }
            else
            {
                msg = String.format("%d个企业无通话,", successEntCount);
            }
        }
        if(failEntCount > 0)
        {
            msg = String.format("%d个企业检查录音时发生异常", failEntCount);
        }
        return msg.replaceAll(",$", "");
    }

    public PlatformRecordCheckResultPo getCheckResult()
    {
        PlatformRecordCheckResultPo resultPo = new PlatformRecordCheckResultPo();
        resultPo.setPlatformId(this.platformId);
        resultPo.setPlatformName(this.platformName);
        resultPo.setCheckTime(this.checkTime);
        resultPo.setTimeUsage(this.timeUsage);
        resultPo.setBeginTime(this.startTime);
        resultPo.setEndTime(this.endTime);
        resultPo.setResult(this.result);
        resultPo.setComment(this.comment);
        if(!result)
            return resultPo;
        resultPo.setCheckEntCount(this.entRecordCheckResultList.size());
        int failEntCount = 0;
        int checkCount = 0;
        int successCount = 0;
        int notIndexCount = 0;
        int notFileCount = 0;
        int notBakIndexCount = 0;
        int notBakFileCount = 0;
        for(EntRecordCheckResultSumVo vo : this.entRecordCheckResultList)
        {
            if(!vo.isResult())
                failEntCount++;
            else
            {
                checkCount += vo.getAllRecordCount();
                successCount += vo.getSuccessList().size();
                notIndexCount += vo.getNotIndexList().size();
                notFileCount += vo.getNotFileList().size();
                notBakIndexCount += vo.getNotBakIndexList().size();
                notBakFileCount += vo.getNotBakFileList().size();
            }
        }
        resultPo.setFailEntCount(failEntCount);
        resultPo.setCheckCount(checkCount);
        resultPo.setSuccessCount(successCount);
        resultPo.setNotIndexCount(notIndexCount);
        resultPo.setNotFileCount(notFileCount);
        resultPo.setNotBakIndexCount(notBakIndexCount);
        resultPo.setNotBakFileCount(notBakFileCount);
        return resultPo;
    }
}
