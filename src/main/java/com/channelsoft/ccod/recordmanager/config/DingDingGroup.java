package com.channelsoft.ccod.recordmanager.config;

import java.util.List;

/**
 * @ClassName: DingDingGroup
 * @Author: lanhb
 * @Description: 用来定义钉钉群配置
 * @Date: 2020/4/6 15:17
 * @Version: 1.0
 */
public class DingDingGroup {

    public String webHookToken;  //用来定义接受消息的钉钉群智能机器人的webHookToken

    public String tag; //向群发送消息时自动添加的标签

    public boolean atAll; //在发送消息时是否需要@所有群的成员

    public List<String> atList; //如果不需要@所有的成员，需要单个@的群成员的手机号

    public String getWebHookToken() {
        return webHookToken;
    }

    public void setWebHookToken(String webHookToken) {
        this.webHookToken = webHookToken;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isAtAll() {
        return atAll;
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    public List<String> getAtList() {
        return atList;
    }

    public void setAtList(List<String> atList) {
        this.atList = atList;
    }
}
