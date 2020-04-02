package com.channelsoft.ccod.recordmanager.backup.vo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName: StoredRecordFileVo
 * @Author: lanhb
 * @Description: 用来定义保存在磁盘上的企业录音文件相关信息的类
 * @Date: 2020/4/1 7:11
 * @Version: 1.0
 */
public class StoredRecordFileVo {

    private String enterpriseId;     //企业ID

    private String callDate; //呼叫日期

    private String fileSavePath; //录音文件存放完整路径

    private String storeDir; //录音的存储目录

    private String fileName; //录音的存储文件名

    private String recordIndex; //录音索引

    public StoredRecordFileVo(String enterpriseId, Date callDate, String fileSavePath, String recordIndex)
    {
        this.enterpriseId = enterpriseId;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        this.callDate = sf.format(callDate);
        this.fileSavePath = fileSavePath;
        String[] arr = fileSavePath.replaceAll("\\\\", "/").split("/");
        this.fileName = arr[arr.length - 1];
        this.storeDir = fileSavePath.replaceAll(String.format("/%s$", this.fileName), "");
        this.recordIndex = recordIndex;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getStoreDir() {
        return storeDir;
    }

    public void setStoreDir(String storeDir) {
        this.storeDir = storeDir;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRecordIndex() {
        return recordIndex;
    }

    public void setRecordIndex(String recordIndex) {
        this.recordIndex = recordIndex;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }
}
