package com.channelsoft.ccod.recordmanager.monitor.vo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: EntRecordCheckResultSumVo
 * @Author: lanhb
 * @Description: 企业录音检查结果
 * @Date: 2020/4/11 19:26
 * @Version: 1.0
 */
public class EntRecordCheckResultSumVo {

    private String enterpriseId;     //企业ID

    private String enterpriseName;   //企业名

    private Date checkTime; //检查时间

    private boolean hasBak;         //是否有备份录音

    private Date startTime;          //检查的呼叫结束最早时间

    private Date endTime;            //检查的呼叫结束最晚时间

    private boolean result; //检查是否发生异常

    private String comment; //如果检查发生异常,异常原因

    private List<RecordDetailVo> successList; //索引以及录音文件检查成功列表

    private List<RecordDetailVo> notIndexList; //没有录音索引列表

    private List<RecordDetailVo> notFileList; //没有录音文件列表

    private List<RecordDetailVo> notBakIndexList; //没有备份录音索引列表

    private List<RecordDetailVo> notBakFileList; //没有备份录音文件列表

    public EntRecordCheckResultSumVo(EnterpriseVo enterpriseVo, Date checkTime, Date startTime, Date endTime,
                                  List<RecordDetailVo> successList, List<RecordDetailVo> notIndexList,
                                  List<RecordDetailVo> notFileList)
    {
        this.enterpriseId = enterpriseVo.getEnterpriseId();
        this.enterpriseName = enterpriseVo.getEnterpriseName();
        this.checkTime = checkTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = true;
        this.successList = successList;
        this.notFileList = notFileList;
        this.notIndexList = notIndexList;
        this.hasBak = false;
        this.notBakFileList = new ArrayList<>();
        this.notBakIndexList = new ArrayList<>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        this.comment = toString();
    }

    public EntRecordCheckResultSumVo(EnterpriseVo enterpriseVo, Date checkTime, Date startTime, Date endTime,
                                  List<RecordDetailVo> successList, List<RecordDetailVo> notIndexList,
                                  List<RecordDetailVo> notFileList, List<RecordDetailVo> notBakIndexList,
                                  List<RecordDetailVo> notBakFileList)
    {
        this.enterpriseId = enterpriseVo.getEnterpriseId();
        this.enterpriseName = enterpriseVo.getEnterpriseName();
        this.checkTime = checkTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = true;
        this.successList = successList;
        this.notFileList = notFileList;
        this.notIndexList = notIndexList;
        this.hasBak = true;
        this.notBakFileList = notBakIndexList;
        this.notBakIndexList = notBakFileList;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        this.comment = toString();
    }

    protected EntRecordCheckResultSumVo(EnterpriseVo enterpriseVo, String erorrMsg)
    {
        this.enterpriseId = enterpriseVo.getEnterpriseId();
        this.enterpriseName = enterpriseVo.getEnterpriseName();
        this.comment = String.format("检查%s[%s]录音文件失败:%s", this.enterpriseId, this.enterpriseName, erorrMsg);
    }

    public static EntRecordCheckResultSumVo fail(EnterpriseVo enterpriseVo, Exception e)
    {
        return new EntRecordCheckResultSumVo(enterpriseVo, e.getMessage());
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public boolean isHasBak() {
        return hasBak;
    }

    public void setHasBak(boolean hasBak) {
        this.hasBak = hasBak;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<RecordDetailVo> getSuccessList() {
        return successList;
    }

    public void setSuccessList(List<RecordDetailVo> successList) {
        this.successList = successList;
    }

    public List<RecordDetailVo> getNotIndexList() {
        return notIndexList;
    }

    public void setNotIndexList(List<RecordDetailVo> notIndexList) {
        this.notIndexList = notIndexList;
    }

    public List<RecordDetailVo> getNotFileList() {
        return notFileList;
    }

    public void setNotFileList(List<RecordDetailVo> notFileList) {
        this.notFileList = notFileList;
    }

    public List<RecordDetailVo> getNotBakIndexList() {
        return notBakIndexList;
    }

    public void setNotBakIndexList(List<RecordDetailVo> notBakIndexList) {
        this.notBakIndexList = notBakIndexList;
    }

    public List<RecordDetailVo> getNotBakFileList() {
        return notBakFileList;
    }

    public void setNotBakFileList(List<RecordDetailVo> notBakFileList) {
        this.notBakFileList = notBakFileList;
    }

    public int getAllRecordCount()
    {
        return this.successList.size() + this.notIndexList.size() + this.notFileList.size()
                + this.notBakIndexList.size() + this.notBakFileList.size();
    }

    @Override
    public String toString()
    {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        if(result)
        {
            String msg = String.format("%s-%s:一共检查了%s[%s]%d通呼叫,%d通正常",	sf.format(startTime), sf.format(endTime),
                    enterpriseName, enterpriseId, getAllRecordCount(), successList.size());
            if(this.notIndexList.size() > 0)
                msg = String.format("%s,%d通没有录音索引", msg, this.notIndexList.size());
            if(this.notFileList.size() > 0)
                msg = String.format("%s,%d通没有录音文件", msg, this.notFileList.size());
            if(this.notBakIndexList.size() > 0)
                msg = String.format("%s,%d没有备份录音索引", msg, this.notBakIndexList.size());
            if(this.notBakFileList.size() > 0)
                msg = String.format("%s,%d没有备份录音文件", msg, this.notBakFileList.size());
            return msg;
        }
        return this.comment;
    }
}
