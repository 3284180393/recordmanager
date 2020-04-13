package com.channelsoft.ccod.recordmanager.backup.vo;

import com.channelsoft.ccod.recordmanager.constant.BackupMethod;

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

    private String grokPattern; //用来匹配的gork pattern

    private BackupMethod backupMethod;  //备份方式

    private String backupSavePath; //存储路径

    private boolean result; //备份结果

    private String comment; //备注

    private boolean verifyResult; //验证结果

    private String verifyComment; //验证结果说明

    public StoredRecordFileVo(String enterpriseId, Date callDate, String fileSavePath, String recordIndex, String grokPattern)
    {
        this.enterpriseId = enterpriseId;
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        this.callDate = sf.format(callDate);
        this.fileSavePath = fileSavePath;
        String[] arr = fileSavePath.replaceAll("\\\\", "/").split("/");
        this.fileName = arr[arr.length - 1];
        this.storeDir = fileSavePath.replaceAll(String.format("/%s$", this.fileName), "");
        this.recordIndex = recordIndex;
        this.grokPattern = grokPattern;
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

    public String getGrokPattern() {
        return grokPattern;
    }

    public void setGrokPattern(String grokPattern) {
        this.grokPattern = grokPattern;
    }

    public BackupMethod getBackupMethod() {
        return backupMethod;
    }

    public void setBackupMethod(BackupMethod backupMethod) {
        this.backupMethod = backupMethod;
    }

    public String getBackupSavePath() {
        return backupSavePath;
    }

    public void setBackupSavePath(String backupSavePath) {
        this.backupSavePath = backupSavePath;
    }

    public boolean isVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(boolean verifyResult) {
        this.verifyResult = verifyResult;
    }

    public String getVerifyComment() {
        return verifyComment;
    }

    public void setVerifyComment(String verifyComment) {
        this.verifyComment = verifyComment;
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

    public String getFileBackupPath(String backupRootPath)
    {
        return String.format("%s%s", backupRootPath, this.getFileSavePath());
    }
}
