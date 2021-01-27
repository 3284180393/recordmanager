package com.channelsoft.ccod.recordmanager.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.util.List;

/**
 * @ClassName: DingDing
 * @Author: lanhb
 * @Description: 用来定义用来交互的钉钉相关配置
 * @Date: 2020/4/6 15:53
 * @Version: 1.0
 */
public class DingDing {

    public List<DingDingGroup> group; //用来定义通知用的钉钉群

    public boolean byScript;

    public String scriptPath;

    public List<DingDingGroup> getGroup() {
        return group;
    }

    public void setGroup(List<DingDingGroup> group) {
        this.group = group;
    }

    public boolean isByScript() {
        return byScript;
    }

    public void setByScript(boolean byScript) {
        this.byScript = byScript;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public boolean isReportByDingDing(){
        if(byScript){
            Assert.isTrue(StringUtils.isNotBlank(scriptPath), "dingding.script-path can not be blank");
            File file = new File(scriptPath);
            Assert.isTrue(file.exists(), String.format("dingding script %s not exist", scriptPath));
            Assert.isTrue(file.isFile(), String.format("%s not a script for dingding", scriptPath));
            return true;
        }
        if(group != null && group.size() > 0){
            for(DingDingGroup ding : group){
                Assert.isTrue(StringUtils.isNotBlank(ding.webHookToken), "webHookToken of dingding can not be blank");
                Assert.isTrue(StringUtils.isNotBlank(ding.tag), "tag of dingding can not be blank");
            }
            return true;
        }
        return false;
    }
}
