package com.channelsoft.ccod.recordmanager.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.File;

/**
 * @ClassName: Wechat
 * @Author: lanhb
 * @Description: 用来定义微信相关配置
 * @Date: 2021/1/26 17:52
 * @Version: 1.0
 */
public class Wechat {

    public String scriptPath;

    public String scriptName;

    public String logFile;

    public String wechatTag;

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getLogFile() {
        return logFile;
    }

    public void setLogFile(String logFile) {
        this.logFile = logFile;
    }

    public String getWechatTag() {
        return wechatTag;
    }

    public void setWechatTag(String wechatTag) {
        this.wechatTag = wechatTag;
    }

    public boolean isReportByWechat(){
        if(StringUtils.isBlank(scriptName) && StringUtils.isBlank(scriptPath) && StringUtils.isBlank(logFile) && StringUtils.isBlank(wechatTag)){
            return false;
        }
        Assert.isTrue(StringUtils.isNotBlank(scriptName), "script-name can not be blank for wechat");
        Assert.isTrue(StringUtils.isNotBlank(scriptPath), "script-path can not be blank for wechat");
        Assert.isTrue(StringUtils.isNotBlank(logFile), "log-file can not be blank for wechat");
        Assert.isTrue(StringUtils.isNotBlank(wechatTag), "wechat-tag can not be blank for wechat");
        String filePath = String.format("%s/%s", scriptPath, scriptName).replaceAll("//", "/");
        File file = new File(filePath);
        Assert.isTrue(file.exists(), String.format("wechat script file %s not exist", filePath));
        Assert.isTrue(file.isFile(), String.format("%s is not a file for wechat script", filePath));
        return true;
    }
}
