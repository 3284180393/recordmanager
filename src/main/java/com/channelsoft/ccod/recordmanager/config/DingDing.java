package com.channelsoft.ccod.recordmanager.config;

import java.util.List;

/**
 * @ClassName: DingDing
 * @Author: lanhb
 * @Description: 用来定义用来交互的钉钉相关配置
 * @Date: 2020/4/6 15:53
 * @Version: 1.0
 */
public class DingDing {

    private List<DingDingGroup> group; //用来定义通知用的钉钉群

    public List<DingDingGroup> getGroup() {
        return group;
    }

    public void setGroup(List<DingDingGroup> group) {
        this.group = group;
    }

    public boolean byScript;

    public String scriptPath;

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
}
